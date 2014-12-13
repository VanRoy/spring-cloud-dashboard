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
    .controller('circuitBreakerCtrl',  ['$scope',
        function ($scope) {

        var stream = "/turbine.stream";

        // commands
        window.hystrixMonitor = new HystrixCommandMonitor('dependencies', {includeDetailIcon:false});

        // sort by error+volume by default
        hystrixMonitor.sortByErrorThenVolume();

        // start the EventSource which will open a streaming connection to the server
        var source = new EventSource(stream);

        // add the listener that will process incoming events
        source.addEventListener('message', hystrixMonitor.eventSourceMessageListener, false);

        source.addEventListener('error', function(e) {
            if (e.eventPhase == EventSource.CLOSED) {
                // Connection was closed.
                console.log("Connection was closed on error: " + JSON.stringify(e));
            } else {
                console.log("Error occurred while streaming: " + JSON.stringify(e));
            }
        }, false);

        // thread pool
        window.dependencyThreadPoolMonitor = new HystrixThreadPoolMonitor('dependencyThreadPools');

        dependencyThreadPoolMonitor.sortByVolume();

        // start the EventSource which will open a streaming connection to the server
        var threadSource = new EventSource(stream);

        // add the listener that will process incoming events
        threadSource.addEventListener('message', dependencyThreadPoolMonitor.eventSourceMessageListener, false);

        threadSource.addEventListener('error', function(e) {
            if (e.eventPhase == EventSource.CLOSED) {
                // Connection was closed.
                console.log("Connection was closed on error: " + e);
            } else {
                console.log("Error occurred while streaming: " + e);
            }
        }, false);
    }]);