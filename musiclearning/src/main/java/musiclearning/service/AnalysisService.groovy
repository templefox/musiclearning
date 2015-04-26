package musiclearning.service

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET;

import java.awt.GraphicsConfiguration.DefaultBufferCapabilities;
import java.nio.file.Files;

import javax.servlet.ServletRequest;

import spark.Request

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.codehaus.groovy.classgen.ReturnAdder;

import com.musicg.fingerprint.FingerprintManager;
import com.musicg.graphic.GraphicRender;
import com.musicg.processor.TopManyPointsProcessorChain;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader
import com.musicg.wave.extension.Spectrogram;

class AnalysisService implements Service {
	def id;
	def suffix ="src/main/resources/web-content/temp/"
	def images = [:];
	@Override
	def execute(Request req) {
		id = req.session().id();
		images.graphics = [];
		
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
					analyticGenerateGraph(item.getName(), stream);
				}
			}
		}
		
		images.graphics.each { value->
			def isRefreshed = false;
			while(!isRefreshed){
				
				def http = new HTTPBuilder("http://127.0.0.1:8080/${value.path}");
				
				http.request(GET) { r ->
					response.success ={ isRefreshed = true }
					response.'404' = {}
					response.failure = { rf -> println "${rf.statusLine}";}
				}
				Thread.sleep(500);
			}
		}
		println images
		return images;
	}
	
	def analyticGenerateGraph(String name, InputStream inputStream){
		File file = File.createTempFile("pre", null);
		byte[] wav = IOUtils.toByteArray(inputStream);
		FileUtils.writeByteArrayToFile(file, wav);
		
		Wave wave = new Wave(file.getAbsolutePath());
		
		file.delete();
		
		Spectrogram gram = wave.getSpectrogram();
		GraphicRender render = new GraphicRender();
		FingerprintManager fingerprintManager=new FingerprintManager();
		
		render.renderSpectrogramData(gram.getNormalizedSpectrogramData(), "${suffix}${id}${name}_n.jpg");
		images.graphics << [path:"temp/${id}${name}_n.jpg",name:"normalized spectrogram",index:2]
		
		
		render.renderSpectrogramData(gram.getAbsoluteSpectrogramData(), "${suffix}${id}${name}_a.jpg");
		images.graphics << [path:"temp/${id}${name}_a.jpg",name:"absolute spectrogram",index:1]
		
		float timestamp =wave.getBytes().length/wave.getWaveHeader().getBitsPerSample()*8 / wave.getWaveHeader().getSampleRate() /2048;
		timestamp = timestamp>0.015625?0.015625:timestamp;
		render.renderWaveform(wave,timestamp,"${suffix}${id}${name}_f.jpg");
		images.graphics << [path:"temp/${id}${name}_f.jpg",name:"wave form",index:0]
		
		fingerprintManager.saveFingerprintAsFile(wave.getFingerprint(), "${suffix}${id}${name}.fingerprint");
		images.fingerprint=[path:"temp/${id}${name}.fingerprint",name:"finger print",index:10]
		
		processIntensities(name, 1, gram, render);
		processIntensities(name, 50, gram, render);
		processIntensities(name, 150, gram, render);
		processIntensities(name, 250, gram, render);
		
	}

	
	def processIntensities(def name,def points, Spectrogram gram ,GraphicRender render){
		TopManyPointsProcessorChain processorChain=new TopManyPointsProcessorChain(gram.getNormalizedSpectrogramData(),points);
		double[][] processedIntensities=processorChain.getIntensities();
		render.renderSpectrogramData(processedIntensities, "${suffix}${id}${name}_pp${points}.jpg");
		images.graphics << [path:"temp/${id}${name}_pp${points}.jpg",name:"processed spectrogram ${points}",index:3+500/points as int]
	}

}
