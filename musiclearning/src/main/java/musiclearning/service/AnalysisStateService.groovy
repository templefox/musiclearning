package musiclearning.service

import groovy.json.JsonSlurper
import spark.Request

class AnalysisStateService implements Service {

	@Override
	public Object execute(Request req) {
		def root = new JsonSlurper().parseText(req.body())
		def name = root.name;
		def id = req.session().id()

		File file = new File("src/main/resources/web-content/temp/");
		
		println file.exists();
		def fileList = file.listFiles()
		println fileList.any { it.getName().contains("${name}") }
		if (fileList.any { it.getName().contains("${name}") }) {
			def images = [:];
			images.graphics = [];

			images.graphics << [path:"temp/${id}${name}_n.jpg",name:"normalized spectrogram",index:2]
			images.graphics << [path:"temp/${id}${name}_a.jpg",name:"absolute spectrogram",index:1]
			images.graphics << [path:"temp/${id}${name}_f.jpg",name:"wave form",index:0]
			images.fingerprint=[path:"temp/${id}${name}.fingerprint",name:"finger print",index:10]

			[1, 50, 150, 250].each { points->
				images.graphics << [path:"temp/${id}${name}_pp${points}.jpg",name:"processed spectrogram ${points}",index:3+500/points as int]
			}
			
			println images
			return images
		}else{
			println "Wrong states"
			return null;		
		}
	}
}
