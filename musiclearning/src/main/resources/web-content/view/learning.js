var learningCtrl = function($scope, $http, $timeout, $interval) {
	var onLearning = false;
	var timeOutTask;
	var valAccWindow = new cnnutil.Window(100);
	$scope.count = 0;
	var layer_defs = [];
	layer_defs.push({type : 'input',out_sx : 128,out_sy : 128,out_depth : 1});
	layer_defs.push({type : 'conv',sx : 64,filters : 12,stride : 2,pad : 2,activation : 'relu'});
	layer_defs.push({type : 'pool',sx : 64,stride : 6});
	layer_defs.push({type : 'conv',	sx : 32,filters : 12,stride : 2,pad : 2,activation : 'relu'	});
	layer_defs.push({type : 'pool',	sx : 16,stride:3});
	layer_defs.push({type : 'softmax',num_classes : 10});
	
	var net = $scope.net = new convnetjs.Net();
	net.makeLayers(layer_defs);

	var trainer = $scope.trainer = new convnetjs.SGDTrainer(net, {
		method : 'adadelta',
		batch_size : 20,
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
		$http.get('sys/RanFileToWave').success(
				function(response) {
						$scope.modules = response;
						
						var vol = new convnetjs.Vol(512, 512, 1, 0);
						for (var i = 0; i < 512; i++) {
							for (var j = 0; j < 512; j++) {
								vol.set(response.amplitudes.pos[i],
										response.amplitudes.neg[j], 1, 1);
							}
						}

						var label = response.type;

						var preparedPost = {vol:vol,label:label,trainer:trainer,net:net};
						//console.log(vol, label);

						//Start train
						var stats = trainer.train(preparedPost.vol, preparedPost.label);
						
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
musicLearning.controller('LearningCtrl', [ '$scope', "$http", "$timeout","$interval", 
		learningCtrl ]);