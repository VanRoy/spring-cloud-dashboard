/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

angular.module('springCloudDashboard')
	.filter('timeInterval', function() {
		function padZero(i,n) {
			var s = i + "";
		    while (s.length < n) s = "0" + s;
		    return s;
		}
		
		return function(input) { 
			var s = input || 0;
			var d = padZero(Math.floor(s / 86400000),2);
			var h = padZero(Math.floor(s  % 86400000 / 3600000),2);
			var m = padZero(Math.floor(s  % 3600000 / 60000),2);
			var s = padZero(Math.floor(s  % 60000 / 1000),2);
		    return d + ':' + h + ':' + m + ':' + s;
		} 
	})
	.filter('classNameLoggerOnly', function() { 
		return function(input, active) { 
			if (!active) {
				return input;
			}
			var result = [];
			for (var j in input) {
				var name = input[j].name;
				var i = name.lastIndexOf('.') + 1;
				if ( name.charAt(i) === name.charAt(i).toUpperCase() ) {
					result.push(input[j]);
				}
			}
			return result;
		}
	})
	.filter('capitalize', function() { 
		return function(input, active) { 
			var s = input + "";
			return s.charAt(0).toUpperCase() + s.slice(1);
		}
	})
	.filter('flatten', function($filter) {
		var flatten = function (obj, prefix) {
			if (obj instanceof Date) {
				obj = $filter('date')(obj, 'dd.MM.yyyy HH:mm:ss');
			}
			if (typeof obj === 'boolean' || typeof obj === 'string' || typeof obj === 'number') {
				return (prefix ? prefix + ': ' : '') + obj;
			}

			var result = '';
			var first = true;
			angular.forEach(obj, function (value, key) {
				if (angular.isString(value) && (key === 'time' || key === 'timestamp')) {
					if (/^\d+$/.test(value)) {
						value = new Date(value * 1000);
					} else if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\+\d{4}$/.test(value)) {
						value = new Date(value);
					}
				}

				if (first) {
					first = false;
				} else {
					result = result + '\n';
				}

				result = result + flatten(value, prefix ? prefix + '.' + key : key);
			});

			return result;
		};
		return flatten;
	})
	.filter('joinArray', function() {
		return function (input, separator) {
			if (!Array.isArray(input) ) {
				return input;
			} else {
				return input.join(separator);
			}
		};
	})
	.filter('humanBytes', function () {
		var units = { B: Math.pow(1024, 0)
			, K: Math.pow(1024, 1)
			, M: Math.pow(1024, 2)
			, G: Math.pow(1024, 3)
			, T: Math.pow(1024, 4)
			, P: Math.pow(1024, 5)
		};

		return function (input, unit) {
			input = input || 0;
			unit = unit || 'B';

			var bytes = input * (units[unit] || 1 );

			var chosen = 'B';
			for (var u in units) {
				if (units[chosen] < units[u] && bytes >= units[u]) {
					chosen = u;
				}
			}

			return (bytes / units[chosen]).toFixed(1).replace(/\.0$/, '').replace(/,/g, '') + chosen;
		};
	})
	.filter('capitalize', function () {
		return function (input) {
			var s = input + '';
			return s.charAt(0).toUpperCase() + s.slice(1);
		};
	});