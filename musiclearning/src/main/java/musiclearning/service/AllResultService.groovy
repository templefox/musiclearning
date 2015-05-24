package musiclearning.service

import spark.Request

class AllResultService implements Service {
	def ret = AllStates.instance.value
	Integer count = AllStates.instance.count
	@Override
	public Object execute(Request req) {
		def r = [:]
		r.possible = ret
		r.type = "all in 1000"
		r.actual_type="all in ${count}"
		r.sum = ret.values().sum();
		return r;
	}

}
