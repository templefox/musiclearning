var musicLearning = angular.module('MusicLearning',['ui.bootstrap','angularFileUpload']);



var init = function($scope,$http){
	
	function getPage(index){
		var uri;
		for(module in $scope.modules){
			var m = $scope.modules[module]
			if(m.index==index){
				uri = "view/"+m.name+".html";
				break;
			}
		}
		return uri;
	}
	
	$scope.select = function(index){
		console.log(index);
		$scope.index = index;
		$scope.current = getPage(index);
	}
	
	$scope.main = function(){
		$scope.current = "view/main.html";
	}

	$http.get('sys/module').success(
		function(modules) {
			console.log(modules);
			$scope.modules = modules;
		}
	);
	
}

musicLearning.controller('Init',init);