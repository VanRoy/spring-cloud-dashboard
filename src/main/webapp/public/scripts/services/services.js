/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by instancelicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

angular.module('springCloudDashboard.services', ['ngResource'])
  	.factory('Applications', ['$resource', function($resource) {
  		return $resource(
  			'api/applications', {}, {
  				query: { method:'GET', isArray:true }
  			});
  		}
  	])

	.service('ApplicationOverview', ['$http', function($http) {
		this.getCircuitBreakerInfo = function(application) {
			return $http.head('/circuitBreaker.stream', {
				params: {appName: application.name}
			}).success(function(response) {
				application.circuitBreaker = true;
			}).error(function() {
				application.circuitBreaker = false;
			});
		};
	}])
  	.factory('Instance', ['$resource', function($resource) {
  		return $resource(
  			'api/instance/:id', {}, {
  				query: { method:'GET'}
  			});
  		}
  	])
    .factory('InstancesHistory', ['$resource', function($resource) {
   		return $resource(
   			'api/registry/history', {}, {
   				query: { method:'GET'}
   			});
   		}
   	])
  	.service('InstanceOverview', ['$http', function($http) {
  		this.getInfo = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/info/').success(function(response) {
  				instance.version = response.version;
  				delete response.version;
  				instance.info = response;
  			}).error(function() {
  				instance.version = '---';
  			});
  		};
  		this.getHealth = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/health/').success(function (response) {
  				instance.health = response.status;
  			}).error(function (response, httpStatus) {
  				if (httpStatus === 503) {
  					instance.health = response.status;
  				} else if (httpStatus === 404) {
  					instance.health = 'OFFLINE';
  				} else {
  					instance.health = 'UNKNOWN';
  				}
  			});
  		};
  	}])
  	.service('InstanceDetails', ['$http', function($http) {
  		this.getInfo = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/info/');
  		};
  		this.getMetrics = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/metrics/');
  		};
  		this.getEnv = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/env/');
  		};
  		this.getHealth = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/health/');
  		};
		this.getCircuitBreakerInfo = function(instance) {
			return $http.head('/circuitBreaker.stream', {
				params: {instanceId: instance.id}
			});
		};
  	}])
  	.service('InstanceLogging', ['$http' , 'Jolokia', function($http, jolokia) {
  		var LOGBACK_MBEAN = 'ch.qos.logback.classic:Name=default,Type=ch.qos.logback.classic.jmx.JMXConfigurator';
  		
  		this.getLoglevel = function(instance, loggers) {
  			var requests = [];
  			for (var j in loggers) {
  				requests.push({ type: 'exec', mbean: LOGBACK_MBEAN, operation: 'getLoggerEffectiveLevel', arguments: [ loggers[j].name ] })
  			}
  			return jolokia.bulkRequest('api/instance/'+ instance.id + '/jolokia/', requests);
  		};
  		
  		this.setLoglevel = function(instance, logger, level) {
  			return jolokia.exec('api/instance/'+ instance.id + '/jolokia/', LOGBACK_MBEAN, 'setLoggerLevel' , [ logger, level] );
  		};
  		
  		this.getAllLoggers = function(instance) {
  			return jolokia.readAttr('api/instance/'+ instance.id + '/jolokia/', LOGBACK_MBEAN, 'LoggerList'); 
  		}
  	}])
  	.service('InstanceJMX', ['$rootScope', 'Abbreviator', 'Jolokia', function($rootScope, Abbreviator, jolokia) {
  		this.list = function(instance) {
  			return jolokia.list('api/instance/'+ instance.id + '/jolokia/').then(function(response) {
  					var domains = [];
  					for (var rDomainName in response.value) {
  						var rDomain = response.value[rDomainName];
  						var domain = {name : rDomainName, beans: [] };
  						
  						for (var rBeanName in rDomain ) {
  							var rBean = rDomain[rBeanName];
  							var bean = { id : domain.name + ':' + rBeanName,
  									     name : '', 
  									     nameProps: {},
  										 description : rBean.desc,
  										 operations : rBean.op,
  										 attributes : rBean.attr
  										 };
  						
  							var name = '';
  							var type = '';
  							var parts = rBeanName.split(',');
  							for (var i in parts ) {
  								var tokens = parts[i].split('=');
								if (tokens[0].toLowerCase() === 'name') {
									name = tokens[1];
								} else{
									bean.nameProps[tokens[0]] = tokens[1];
									if ((tokens[0].toLowerCase() === 'type' || tokens[0].toLowerCase() == 'j2eetype') && type.length ==0 ) {
										type = tokens[1];
									}
								}
  							}
  							
  							if (name.length !== 0) {
  								bean.name = name;
  							} 
  							if ( type.length !== 0) {
  								if (bean.name  !== 0) {
  									bean.name += ' ';
  								}
  								bean.name += '[' + Abbreviator.abbreviate(type, '.', 25, 1, 1) + ']';
  							} 
  							
  							if (bean.name.length === 0) {
  								bean.name = rBeanName;
  							}
  						
	  						domain.beans.push(bean);
  						}
  						
  						domains.push(domain);
  					}
  					
  					return domains;
  				}, function(response) {
  					return response;
  				});
  		};
  		
  		this.readAllAttr = function(instance, bean) { 			
  			return jolokia.read('api/instance/'+ instance.id + '/jolokia/', bean.id)
  		};
  		
  		this.writeAttr = function(instance, bean, attr, val) {
  			return jolokia.writeAttr('api/instance/'+ instance.id + '/jolokia/', bean.id, attr, val);
  		};
  		
  		this.invoke = function(instance, bean, opname, args) {
  			return jolokia.exec('api/instance/'+ instance.id + '/jolokia/', bean.id, opname, args);
		}
  		
  	}])
  	.service('Abbreviator', [function() {
  		  function _computeDotIndexes(fqName, delimiter, preserveLast) {
		    var dotArray = [];
		    
		    //iterate over String and find dots
		    var lastIndex = -1;
		    do {
		      lastIndex = fqName.indexOf(delimiter, lastIndex + 1);
		      if (lastIndex !== -1) {
		        dotArray.push(lastIndex);
		      }
		    } while (lastIndex !== -1)

		    // remove dots to preserve more than the last element
		    for (var i = 0; i < preserveLast -1; i++ ) {
		    	dotArray.pop();
		    }
		    	
		    return dotArray;
		  }

		  function _computeLengthArray(fqName, targetLength, dotArray, shortenThreshold) {
			var lengthArray = [];
		    var toTrim = fqName.length - targetLength;

		    for (var i = 0; i < dotArray.length; i++) {
		      var previousDotPosition = -1;
		      if (i > 0) {
		        previousDotPosition = dotArray[i - 1];
		      }

		      var len = dotArray[i] - previousDotPosition - 1;
		      var newLen = (toTrim > 0 && len > shortenThreshold ? 1 : len);
		      
		      toTrim -= (len - newLen);
		      lengthArray[i] = newLen + 1;
		    }

		    var lastDotIndex = dotArray.length - 1;
		    lengthArray[dotArray.length] = fqName.length - dotArray[lastDotIndex];
		    
		    return lengthArray;
		  }
  		
  		this.abbreviate = function(fqName, delimiter, targetLength, preserveLast, shortenThreshold) {
  		    if (fqName.length < targetLength) {
  		      return fqName;
  		    }

  		    var dotIndexesArray = _computeDotIndexes(fqName, delimiter, preserveLast);

  		    if (dotIndexesArray.length === 0) {
  		      return fqName;
  		    }

  		    var lengthArray = _computeLengthArray(fqName, targetLength, dotIndexesArray, shortenThreshold);

  		    var result = "";
  		    for (var i = 0; i <= dotIndexesArray.length; i++) {
  		    	if (i === 0 ) {
  		    		result += fqName.substr(0, lengthArray[i] -1);
  		    	} else {
  		    		result += fqName.substr(dotIndexesArray[i - 1], lengthArray[i]);
  		    	}
  		    }
  		    
  		    return result;
  		}
  	}])
  	.service('Jolokia', [ '$q' , '$rootScope', function($q){
  		var outer = this;
  		var j4p = new Jolokia();
  		
  		
  		this.bulkRequest = function(url, requests) {
  			var deferred = $q.defer();
  			deferred.notify(requests);

  			var hasError = false;
  			var responses = [];
  			
  			j4p.request( requests,
  					 {	url: url,
  						method: 'post',
  						success: function (response) {
  							responses.push(response);
  							if (responses.length >= requests.length) {
  								if (!hasError) {
  									deferred.resolve(responses);
  								} else {
  									deferred.resolve(responses);
  								}
  							}
  						},
  						error: function (response) {
  							hasError = true;
  							responses.push(response);
  							if (responses.length >= requests.length) {
  								deferred.reject(responses);
  							}
  						}
  					 });
  			
  			return deferred.promise;
  		};
  		
  		
  		this.request = function(url, request) {
  			var deferred = $q.defer();
  			deferred.notify(request);

  			j4p.request( request,
  					 {	url: url,
  						method: 'post',
  						success: function (response) {
  							deferred.resolve(response);
  						},
  						error: function (response) {
  							deferred.reject(response);
  						}
  					 });
  			
  			return deferred.promise;
  		};
  		
  		this.exec = function(url, mbean, op, args) {
  			return outer.request(url, { type: 'exec', mbean: mbean, operation: op, arguments: args });
  		};
  		
  		this.read = function(url, mbean) {
  			return outer.request(url, { type: 'read', mbean: mbean });
  		};
  		
  		this.readAttr = function(url, mbean, attr) {
  			return outer.request(url, { type: 'read', mbean: mbean, attribute: attr });
  		};
  		
  		this.writeAttr = function(url, mbean, attr, val) {
  			return outer.request(url, { type: 'write', mbean: mbean, attribute: attr, value: val });
  		};
  		
  		this.list = function(url) {
  			return outer.request(url, { type: 'list' });
  		}
  	}])
  	.service('MetricsHelper', [function() {
  		this.find = function (metrics, regexes, callbacks) {
  			for (var metric in metrics) {
  				for (var i in regexes) {
  						var match = regexes[i].exec(metric);
						if (match != null) {
							callbacks[i](metric, match, metrics[metric]);
							break;
						}
					}
				}
			}
  	}])
  	.service('InstanceThreads', ['$http', function($http) {
  		this.getDump = function(instance) {
  			return $http.get('api/instance/'+ instance.id + '/dump/');
  		}
  	}	]);
