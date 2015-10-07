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

angular.module('springCloudDashboard', [
  'ngResource',
  'ngRoute',
  'ui.router',
  'ui.bootstrap',
  'springCloudDashboard.services',
  'nvd3ChartDirectives'
])
  	.config(function ($stateProvider, $urlRouterProvider) {
  		$urlRouterProvider
  			.when('/', '/app')
  			.otherwise('/');
  		$stateProvider
	  		.state('overview', {
	  			url: '/app',
	  			templateUrl: 'views/overview.html',
	  			controller: 'overviewCtrl'
	  		})
			.state('overview.select', {
				url: '/:id',
				templateUrl: 'views/overview.selected.html',
				controller: 'overviewSelectedCtrl'
			})
  			.state('about', {
  				url: '/about',
  				templateUrl: 'views/about.html'
  			})
  			.state('apps', {
  				abstract:true,
  				url: '/app/:appId/instance/:instanceId',
  				controller: 'appsCtrl',
  				templateUrl: 'views/apps.html',
  				resolve: {
  			      instance: ['$stateParams', 'Instance' , function($stateParams, Instance) {
  			          return Instance.query({id: $stateParams.instanceId}).$promise;
  			      }]
  			   }
  			})
            .state('history', {
                url: '/history',
                templateUrl: 'views/apps/history.html',
                controller: 'appsHistoryCtrl'
            })
			.state('circuit-breaker', {
				url: '/circuit-breaker/:type/:id',
				templateUrl: 'views/circuit-breaker/index.html',
				controller: 'circuitBreakerCtrl'
			})
  			.state('apps.details', {
  				url: '/details',
  				templateUrl: 'views/apps/details.html',
  				controller: 'detailsCtrl'
  			})
  			.state('apps.details.metrics', {
  				url: '/metrics',
  				templateUrl: 'views/apps/details/metrics.html',
  				controller: 'detailsMetricsCtrl'
  			})
  			.state('apps.details.classpath', {
  				url: '/classpath',
  				templateUrl: 'views/apps/details/classpath.html',
  				controller: 'detailsClasspathCtrl'
  			})
			.state('apps.env', {
				url: '/env',
				templateUrl: 'views/apps/environment.html',
				controller: 'environmentCtrl'
			})
  			.state('apps.logging', {
  				url: '/logging',
  				templateUrl: 'views/apps/logging.html',
  				controller: 'loggingCtrl'
  			})
  			.state('apps.jmx', {
  				url: '/jmx',
  				templateUrl: 'views/apps/jmx.html',
  				controller: 'jmxCtrl'
  			})
  			.state('apps.threads', {
  				url: '/threads',
  				templateUrl: 'views/apps/threads.html',
  				controller: 'threadsCtrl'
  			})
			.state('apps.trace', {
				url: '/trace',
				templateUrl: 'views/apps/trace.html',
				controller: 'traceCtrl'
			});
  	})
  	.run(function ($rootScope, $state, $stateParams, $log) {
  		$rootScope.$state = $state;
  		$rootScope.$stateParams = $stateParams;
  	});
