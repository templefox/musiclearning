package musiclearning.service

import java.lang.Thread.UncaughtExceptionHandler;

import groovyx.gpars.GParsExecutorsPool;
import groovyx.gpars.GParsPool;
import spark.Request
import musiclearning.portal.Portal

class AllAnalysisService implements Service {
	def ret = AllStates.instance.value

	@Override
	public def execute(Request req) {
		File resource = Portal.getResourceFile();
		File samples = new File(resource, "samples");
		ret.clear();
		AllStates.instance.count = 0;
		ClassifyService classifyService = new ClassifyService();

		UncaughtExceptionHandler handler = new UncaughtExceptionHandler(){
			@Override
			void uncaughtException(Thread t, Throwable e){
				e.printStackTrace();
			}
		}
		
		GParsPool.withPool(30,handler) {
			samples.listFiles().eachParallel { File f->
				if (!f.isDirectory()) {
					def result = classifyService.classify0(f.name, f)
					AllStates.instance.count++
					if (result.actual_type.equals(result.type)) {
						ret."${result.type}" = 1 + (ret."${result.type}"?ret."${result.type}":0)
					}
				}
			}
		}
		println "done!"
		//		samples.eachFileRecurse { File f->
		//			if (!f.isDirectory()) {
		//				def result = classifyService.classify(f.name, new FileInputStream(f))
		//				if (result.actual_type.equals(result.type)) {
		//					ret."${result.type}" = 1 + (ret."${result.type}"?ret."${result.type}":0)
		//				}
		//			}
		//		}

		def r = [:]

		r.possible = ret
		r.type = "all in 1000"
		r.actual_type="all in ${AllStates.instance.count}"
		r.sum = ret.values().sum();
		return r;
	}
}
