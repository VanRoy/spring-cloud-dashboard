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
    .controller('circuitBreakerCtrl',  ['$scope', '$stateParams', 'Instance',
        function ($scope, $stateParams, Instance) {

        if($stateParams.type == 'app') {
            var stream = "/circuitBreaker.stream?appName="+$stateParams.id;
            $scope.subtitle = $stateParams.id;
        } else if($stateParams.type == 'instance') {
            var stream = "/circuitBreaker.stream?instanceId="+$stateParams.id;
            var instance = Instance.query({id: $stateParams.id}, function(instance){
                $scope.subtitle = instance.name;
            });
        }

        // commands
        $scope.hystrixMonitor = new HystrixCommandMonitor('dependencies', {includeDetailIcon:false});

        // sort by error+volume by default
        $scope.hystrixMonitor.sortByErrorThenVolume();

        // start the EventSource which will open a streaming connection to the server
        $scope.source = new EventSource(stream);

        // add the listener that will process incoming events
        $scope.source.addEventListener('message', $scope.hystrixMonitor.eventSourceMessageListener, false);

        $scope.source.addEventListener('error', function(e) {
            if (e.eventPhase == EventSource.CLOSED) {
                // Connection was closed.
                console.log("Connection was closed on error: " + JSON.stringify(e));
            } else {
                console.log("Error occurred while streaming: " + JSON.stringify(e));
            }
        }, false);

        // thread pool
        $scope.dependencyThreadPoolMonitor = new HystrixThreadPoolMonitor('dependencyThreadPools');

        $scope.dependencyThreadPoolMonitor.sortByVolume();

        // start the EventSource which will open a streaming connection to the server
        $scope.threadSource = new EventSource(stream);

        // add the listener that will process incoming events
        $scope.threadSource.addEventListener('message', $scope.dependencyThreadPoolMonitor.eventSourceMessageListener, false);

        $scope.threadSource.addEventListener('error', function(e) {
            if (e.eventPhase == EventSource.CLOSED) {
                // Connection was closed.
                console.log("Connection was closed on error: " + e);
            } else {
                console.log("Error occurred while streaming: " + e);
            }
        }, false);

        $scope.$on('$destroy', function clearEventSource() {
            if($scope.source) { $scope.source.close(); delete $scope.source; }
            if($scope.threadSource) { $scope.threadSource.close(); delete $scope.threadSource; }
        })
    }]);