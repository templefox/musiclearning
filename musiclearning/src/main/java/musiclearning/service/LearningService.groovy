package musiclearning.service

import musiclearning.portal.Portal;
import spark.Request

class LearningService implements Service {
	public static def MUSIC_TYPE = ['blues':0,'classical':1,'country':2,'disco':3,'hiphop':4,'jazz':5,
									'metal':6,'pop':7,'reggae':8,'rock':9];

	@Override
	def execute(Request req) {
		File resource = Portal.getResourceFile();
		File samples = new File(resource, "samples");
		
		def bound = 1000;
		File file = samples.listFiles().find{new Random().nextInt(bound--)==0}
		println file.name;
		
		return null;
	}
}
