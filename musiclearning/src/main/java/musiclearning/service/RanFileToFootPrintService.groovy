package musiclearning.service

import com.musicg.fingerprint.FingerprintManager
import com.musicg.wave.Wave;

import musiclearning.portal.Portal;
import spark.Request;
import musiclearning.extension.WaveExtension;

class RanFileToFootPrintService implements Service{
	private Random r = new Random();
	private static  count = 54;
	@Override
	def execute(Request req) {
		File resource = Portal.getResourceFile();
		File samples = new File(resource, "samples");
		
		
		def bound = 1000;
		//File file = samples.listFiles().find{r.nextInt(bound--)==0}
		File file = samples.listFiles()[count]
		count+=11;
		if (count>=200) {
			count%=200
		}
		
		def res = [:]
		
		Wave wave = new Wave(new FileInputStream(file));
		wave.trim(10d, 10d);
//		float timestamp =wave.getBytes().length/wave.getWaveHeader().getBitsPerSample()*8 / wave.getWaveHeader().getSampleRate() /128;

		byte[] result = wave.getFingerprint();
		
		def co = []
		def xx = [];
		def yy = [];
		for(int i = 0;i<result.length;i+=8){
			int _x =  (result[i]<<8) as int;
			_x+= (result[i+1]) as int;
			
			int _y = (result[i+2]<<8) as int;
			_y += (result[i+3]) as int;
			
			int i_intense = (result[i+4]<<24) as int;
			i_intense += (result[i+5]<<16) as int;
			i_intense += (result[i+6]<<8) as int;
			i_intense += (result[i+7]) as int;
			
			float _intense = i_intense;
			_intense /= Integer.MAX_VALUE;
			_intense *= 2**8
			
			xx<<_x;
			yy<<_y;
			co << [x:_x,y:_y,intense:_intense]
		}
		
		println xx.sort()[0];
		println xx.sort()[-1];
		println yy.sort()[0];
		println yy.sort()[-1];
		println co.size();
		
//		println result.pos.length;
//		println result.neg.length;
		
		def type_name = file.name.split("\\.")[0];
		def type_id = LearningService.MUSIC_TYPE.get(type_name);
		
		res.result =  co;
		if (r.nextInt(10)<7) {
			res.cheat = type_id;			
		}
		res.type = type_id;
		res.name = file.name;
		
		return res;
	}
	
}
