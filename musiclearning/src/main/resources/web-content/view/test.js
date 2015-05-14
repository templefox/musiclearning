var about = function($scope,$interval){
	$scope.acc = 0.1;
	$scope.count = 0;
	
	$scope.x;
	$scope.y
	var valAccWindow = new cnnutil.Window(100);
	var layer_defs = [];
	layer_defs.push({type:'input', out_sx:1, out_sy:1, out_depth:10});
	layer_defs.push({type:'fc', num_neurons:6, activation: 'tanh'});
	layer_defs.push({type:'fc', num_neurons:2, activation: 'tanh'});
	layer_defs.push({type:'softmax', num_classes:2});
	
	var net = new convnetjs.Net();
	net.makeLayers(layer_defs);
	
	var trainer = new convnetjs.SGDTrainer(net, {method:'adadelta', batch_size:20, l2_decay:0.001});
	
	$scope.net = net;
	$scope.trainer = trainer;
	
	function shuffle(aArr){
	    var iLength = aArr.length,
	        i = iLength,
	        mTemp,
	        iRandom;
	 
	    while(i--){
	        if(i !== (iRandom = Math.floor(Math.random() * iLength))){
	            mTemp = aArr[i];
	            aArr[i] = aArr[iRandom];
	            aArr[iRandom] = mTemp;
	        }
	    }
	 
	    return aArr;
	}
	function getRandomVolumAndLabels(){
		var vol = new convnetjs.Vol(1, 1, 10, 0);
		var index = shuffle([0,1,2,3,4,5,6,7,8,9]);
		
		//console.log(index,vol);
		for(var i=0;i<5;i++){
			vol.w[index[i]] = 1
		}
		
		console.log(vol);
		return vol;
	}
	
	function getLabel(array){
		var count = 0;
		for(var i = 0;i<5;i++){
			if(array[i]==1)
				count++;
		}
		return count >=3?1:0;
	}
	
	$scope.arrayToString = function arrayToString(array){
		var str = cnnutil.f2t(array[0]);
		for(var i = 1;i< array.length;i++){
			str=str + ","+cnnutil.f2t(array[i]);
		}
		return str;
	}
	
	$scope.click = function(){
		console.log("fuck why")
		var vol = getRandomVolumAndLabels();
		var label = getLabel(vol.w);
		
		//console.log(vol.w,label)
		var stats = $scope.trainer.train(vol,label);
		
		
		
		var yhat = $scope.net.getPrediction();
		
		console.log(yhat === label, yhat);
		
		
		
		valAccWindow.add(yhat === label? 1:0);
		
		$scope.acc = cnnutil.f2t(valAccWindow.get_average());
		$scope.count++;

		var str = $scope.arrayToString(vol.w);
		$scope.x = str;
		
		$scope.y = label;
		$scope.guess = yhat;
		
		console.log(stats);
		console.log($scope.trainer)
		console.log($scope.net);
	}
	
	var inter;
	$scope.start = function(){
		if(inter != null) return;
		inter = $interval($scope.click, 10);
	}
	
	$scope.stop = function(){
		$interval.cancel(inter);
		inter = null;
	}
}

musicLearning.controller('About',about);