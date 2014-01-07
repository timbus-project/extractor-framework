app.controller("MainController", function($scope, $http){
    $scope.machines = [];
    $scope.jobs = [];

    $scope.installedExtractors = [];

    $http({
            method: 'GET',
            url: '/extractors/api/extractors'//'/extractors/api/engine/list'
        }).success(function (result) {
            $scope.installedExtractors = result.extractor;
    });

    $scope.newMachine = null;
    $scope.newMachinePort = null;

    $scope.selectedModule = null;

    $scope.addNew = function() {
        if ($scope.newMachine != null && $scope.newMachine != "" && $scope.newMachinePort != null && $scope.newMachinePort != "") {
            $scope.machines.push({
                fqdn: $scope.newMachine,
                port: $scope.newMachinePort,
                module: $scope.selectedModule.name
            });
            $scope.newMachine = null;
            $scope.newMachinePort = null;
        }
    }

    $scope.removeMachine = function(machine) {
        for(var i = 0; i < $scope.machines.length; ++i) {
            if( $scope.machines[i].hostname == machine.hostname && $scope.machines[i].extractor == machine.extractor && $scope.machines[i].port == machine.port ) {
                $scope.machines.splice(i, 1);
                break;
            }
        }
    }

    $scope.submitRequest = function() {
        console.log("Submitting Request");

        var data = { extractions: $scope.machines };
//        console.log(data);

        $http({
            method: 'POST',
            url: '/extractors/api/extract',
            data: data
        }).success(function (result, status, headers) {
            console.log(result);
            $scope.jobs.push(result);


            // temporary polling code
            var timer = false,
                pollingRequests = {},
                jobsUL = $('ul#jobs'),
                li = $('<li/>').attr('id', 'extraction' + result.id)
                    .append($('<span/>').attr('id', 'extractionReport').append("<b>extraction</b> request #" + result.id + " submitted."))
                    .appendTo(jobsUL);

            pollingRequests['extraction' + result.id] = { url: headers('location'), requestId: result.id };
            timer = setInterval(function() {
                for (pollingId in pollingRequests) {
                    var currentLiElement = jobsUL.children('#extraction' + pollingRequests[pollingId].requestId);
                    $http({
                        method: 'GET',
                        url: pollingRequests[pollingId].url + '/finished'
                    }).success(function (data, fStatus) {
                        switch (fStatus) {
                        case 200:
                            // extraction/conversion complete
                            var conversion = { url: pollingRequests[pollingId].url, requestId: pollingRequests[pollingId].requestId };
                            console.log(conversion);
                            if (currentLiElement.has('span#conversionReport').length === 0) {
                                // extraction complete
                                currentLiElement.find("span#extractionReport").append(' extraction finished. ');
                                currentLiElement.append($('<a/>').append('convert').click(function() {
                                    $http({
                                        method: 'POST',
                                        url: '/populator/api/convert',
                                        data: conversion
                                    }).success(function (conversionResult, cStatus, cHeaders) {
                                        // conversion requested
                                        console.log(conversionResult);
                                        currentLiElement.children('a').remove();
                                        pollingRequests['conversion' + conversionResult.requestId] = {
                                            url: cHeaders('location'),
                                            requestId: conversionResult.requestJob.input.requestId,
                                            conversionId: conversionResult.requestId
                                        };
                                        currentLiElement.append($('<span/>').attr('id', 'conversionReport').append("<b>conversion</b> requested."));
                                        delete pollingRequests[pollingId];
                                    });
                                }).attr('style', 'cursor:pointer;'));
                            } else if (currentLiElement.children("span#conversionReport:contains('finished')").length === 0) {
                                // conversion complete
                                currentLiElement.children("span#conversionReport").append(" conversion finished.");
                            }
                            delete pollingRequests[pollingId];
                            if (pollingRequests.length === 0) {
                                clearInterval(timer);
                                timer = false;
                            }
                            break;
                        case 201:
                            // extraction/conversion ongoing
                            currentLiElement.children("span:not(:has(span#processRunning))").append(' ').append($('<span/>').attr('id', 'processRunning').append('process is running...'));
                            break;
                        default:
                           break;
                        }
                    }).error(function (data, eStatus) {
                        switch (eStatus) {
                        case 404:
                            // extraction/conversion lost in the process
                            currentLiElement.append(' lost.');
                            break;
                        case 500:
                            // extraction/conversion failed
                            currentLiElement.append(' failed.');
                            break;
                        default:
                            break;
                        }
                        delete pollingRequests[pollingId];
                        if (pollingRequests.length === 0) {
                            clearInterval(timer);
                            timer = false;
                        }
                    });
                }
            }, 1000)
            // end of temporary polling code

        });

        $scope.machines = [];
    }
});
