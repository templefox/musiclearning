var m= function($scope, FileUploader, $http) {

	var uploader = $scope.uploader = new FileUploader({
        url: '/sys/classify'
    });
	
//	uploader.filters.push({
//		name: 'customFilter',
//		fn: function(item,options){
//			return true;
//		}
//	});\
	uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
            console.info('onWhenAddingFileFailed', item, filter, options);
        };
        uploader.onAfterAddingFile = function(fileItem) {
            console.info('onAfterAddingFile', fileItem);
        };
        uploader.onAfterAddingAll = function(addedFileItems) {
            console.info('onAfterAddingAll', addedFileItems);
            uploader.queue[0].isReady = uploader.queue[0].isUploading = uploader.queue[0].isSuccess = false;
        };
        uploader.onBeforeUploadItem = function(item) {
            console.info('onBeforeUploadItem', item);
        };
        uploader.onProgressItem = function(fileItem, progress) {
            console.info('onProgressItem', fileItem, progress);
        };
        uploader.onProgressAll = function(progress) {
            console.info('onProgressAll', progress);
        };
        uploader.onSuccessItem = function(fileItem, response, status, headers) {
            console.info('onSuccessItem', fileItem, response, status, headers);
        };
        uploader.onErrorItem = function(fileItem, response, status, headers) {
            console.info('onErrorItem', fileItem, response, status, headers);
        };
        uploader.onCancelItem = function(fileItem, response, status, headers) {
            console.info('onCancelItem', fileItem, response, status, headers);
        };
        uploader.onCompleteItem = function(fileItem, response, status, headers) {
            console.info('onCompleteItem', fileItem, response, status, headers);
            $scope.guess = response.type;
            $scope.truth = fileItem.file.name.split(".")[0]
            $scope.possibility = response.possible;
            $scope.sum = response.sum
            console.info(response);
        };
        uploader.onCompleteAll = function() {
        	uploader.queue[0].remove();
        	console.log(uploader.queue)
            console.info('onCompleteAll');
        };

        console.info('uploader', uploader);
};

musicLearning.controller('mCtrl',['$scope','FileUploader',"$http",m]);