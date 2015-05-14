package musiclearning.service

import com.musicg.wave.Wave;

import musiclearning.portal.Portal;
import spark.Request;
import musiclearning.extension.WaveExtension;

class RanFileToWaveService implements Service{
	
	@Override
	def execute(Request req) {
		File resource = Portal.getResourceFile();
		File samples = new File(resource, "samples");
		
		def bound = 1000;
		File file = samples.listFiles().find{new Random().nextInt(bound--)==0}
		
		def res = [:]
		
		Wave wave = new Wave(new FileInputStream(file));
		
		float timestamp =wave.getBytes().length/wave.getWaveHeader().getBitsPerSample()*8 / wave.getWaveHeader().getSampleRate() /128;

		def result = WaveExtension.getReducedNormalizedAmplitudes(wave,timestamp);
		
		println result.pos.length;
		println result.neg.length;
		
		def type_name = file.name.split("\\.")[0];
		def type_id = LearningService.MUSIC_TYPE.get(type_name);
		
		res.amplitudes = result;
		res.type = type_id;
		res.name = file.name;
		
		return res;
	}
	
}
