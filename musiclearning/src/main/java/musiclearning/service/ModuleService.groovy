package musiclearning.service

import spark.Request
import spark.Session;

class ModuleService implements Service {

	@Override
	def execute(Request req) {
		req.session();

		return [[display:'分析',name:'analysis',index:1],
			[display:'学习',name:'learning',index:2],
			[display:'样例',name:'sample',index:3],
			[display:'关于',name:'about',index:9]]
		.sort{a,b->a.index-b.index};
	}

}
