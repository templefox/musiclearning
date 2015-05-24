package musiclearning.service

import musiclearning.portal.Portal;
import spark.Request

class OnLearningService implements Service {

	@Override
	public Object execute(Request req) {
		def type = req.queryParams "type" as Boolean;
		if (type!=null) {
			Portal.onLearning = true;
		}
		return Portal.onLearning;
	}

}
