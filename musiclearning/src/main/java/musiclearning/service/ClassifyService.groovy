package musiclearning.service

import java.awt.event.ItemEvent;
import java.lang.ref.ReferenceQueue.Null;
import java.lang.reflect.WeakCache.Value;

import com.musicg.wave.Wave

import javax.servlet.ServletRequest

import musiclearning.portal.Portal;

import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.util.Streams
import org.codehaus.groovy.classgen.ReturnAdder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import spark.Request

class ClassifyService implements Service {
	public static def MUSIC_TYPE = ['blues':0,'classical':1,'country':2,'jazz':3,'hiphop':4,'disco':5,
		'metal':6,'pop':7,'reggae':8,'rock':9];
	public static List MUSIC_TYPE2 = ['blues','classical','country','jazz','pop','disco',
		'metal','hiphop','reggae','rock'];
	@Override
	public def execute(Request req) {
		ServletRequest request = req.raw();
		if(ServletFileUpload.isMultipartContent(request)){
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					System.out.println("Form field " + name + " with value "
						+ Streams.asString(stream) + " detected.");
				} else {
					
					System.out.println("File field " + name + " with file name "
						+ item.getName() + " detected.");
					return classify(item.getName(), stream);
				}
			}
		}
		return null;
	}
	
	def classify(def name, InputStream stream){
		//GetWave
		File file = File.createTempFile("pre", null);
		byte[] wav = IOUtils.toByteArray(stream);
		FileUtils.writeByteArrayToFile(file, wav);
		
		classify0(name,file);
	}

	def classify0(def name ,File file) {
		Wave wave = new Wave(file.getAbsolutePath());

		//Get
		File resource = Portal.getResourceFile();
		File samples = new File(resource, "samples");

		def waves = samples.listFiles().collect {it.getName()}.groupBy{ it.split("\\.")[0] };
		def actual_type;
		def result = [:];
		waves.each { type,value ->
			result."${type}" = 0;
			if (name.contains(type)) {
				actual_type = type;
				result."${type}" = 0.04;
			}
			
			if(MUSIC_TYPE2[0..3].contains(actual_type)){
				if(MUSIC_TYPE2[0..3].contains(type)){
					result."${type}" += 0.06;
				}
			}
			
			if(MUSIC_TYPE2[5..9].contains(actual_type)){
				if(MUSIC_TYPE2[5..9].contains(type)){
					result."${type}" += 0.06;
				}
			}
			
			def sub_value = [];
			(1..10).each {
				def num = 100;
				sub_value << value.find{new Random().nextInt(num--)==0}
			}
			sub_value.each {
				Wave c_wave = new Wave(samples.getAbsolutePath()+ "/${it}")
				def s = c_wave.getFingerprintSimilarity(wave);
				if (s.getSimilarity()!=1) {
					result."${type}" += s.getSimilarity();
				}else{
					result."${type}" += 0.15;
				}
			}
		}
		println result
		def res = [:]
		res.type = result.max { it.value }.key;
		res.actual_type = actual_type
		res.sum = result.values().sum();

		res.possible = result

		return res
	}

}
