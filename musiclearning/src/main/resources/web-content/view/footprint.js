var footPrintCtrl = function($scope, $http, $timeout, $interval) {
	var onLearning = false;
	var timeOutTask;
	var valAccWindow = new cnnutil.Window(100);
	$scope.count = 0;
	var layer_defs = [];
	layer_defs.push({type : 'input',out_sx : 256,out_sy : 512,out_depth : 1});
	layer_defs.push({type : 'conv',	sx : 256,filters : 12,stride : 2,pad : 2,activation : 'relu'});
	layer_defs.push({type : 'pool',	sx : 256,stride:2});
	layer_defs.push({type : 'softmax',num_classes : 10});
	
	var net = $scope.net = new convnetjs.Net();
	net.makeLayers(layer_defs);

	var trainer = $scope.trainer = new convnetjs.SGDTrainer(net, {
		method : 'adadelta',
		batch_size : 2,

		l2_decay : 0.001
	});

	$scope.arrayToString = function arrayToString(array) {
		var str = cnnutil.f2t(array[0]);
		for (var i = 1; i < array.length; i++) {
			str = str + "," + cnnutil.f2t(array[i]);
		}
		return str;
	}

	function train() {
		$http.get('sys/RanFileToFootPrint').success(
				function(response) {
						$scope.modules = response;
						
						var vol = new convnetjs.Vol(256,512,1,0);
						for (var i = 0; i < response.result.length;i++){
							var co = response.result[i];
							vol.set(co.x,co.y,co.intense);
						}

						var label = response.type;

						//console.log(vol, label);

						//Start train
						var start = new Date().getTime();
						var stats = trainer.train(vol, label);
						var end = new Date().getTime();
						
						$scope.cost = (end - start)/1000;
						
						var yhat = $scope.net.getPrediction();

						valAccWindow.add(yhat === label ? 1 : 0);

						$scope.acc = cnnutil.f2t(valAccWindow.get_average());
						$scope.count++;

						$scope.x = response.name;

						$scope.y = label;
						$scope.guess = yhat;
						
						if($scope.acc>0.5){
							$scope.stop();
						}

						if (onLearning) {
							$scope.start();
						}else{
							$scope.stop();
						}
						/*var p = new Parallel(preparedPost, { 
							evalPath: 'eval.js' ,
							env:{
								trainer:trainer
						}});
						p.require("test/convnetjs_release/build/convnet-min.js");
						p.require("test/convnetjs_release/demo/util.js");
						p.spawn(function(preparedPost){
						}).then(function (stats){
						});*/
				});
	}

	$scope.start = function() {
		onLearning = true;
		if (onLearning) {
			timeOutTask = $timeout(train,1000);
		}
	}

	$scope.select = function() {
		train();
	}

	$scope.stop = function() {
		onLearning = false;
		$timeout.cancel(timeOutTask);
		timeOutTask = null;
	}
	
	$scope.generateNet = function(){
		$scope.nets = JSON.stringify(net.toJSON());
	}
	
	$scope.removeNet = function(){
		$scope.nets = "removed";
	}
	
}
musicLearning.controller('footprintCtrl', [ '$scope', "$http", "$timeout","$interval", 
                                           footPrintCtrl ]);