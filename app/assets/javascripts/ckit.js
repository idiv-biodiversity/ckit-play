var app = angular.module('CkitApp', []);

app.controller('CkitCtrl', function ($scope,$window) {
    $scope.showJob = function (id) {
        $window.location.assign('/job/' + id)
    }
})
