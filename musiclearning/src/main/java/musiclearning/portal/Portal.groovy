package musiclearning.portal

import static spark.Spark.*;
import groovy.json.JsonBuilder

import java.awt.GraphicsConfiguration.DefaultBufferCapabilities;
import java.rmi.server.LoaderHandler;
import java.security.PublicKey;

import org.apache.commons.io.FileUtils;

import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

class Portal {
		public static onLearning = false;
		public static File getResourceFile(){
			def userHome = System.getProperty("user.home");
			File file = new File("${userHome}/musiclearning-resource/");
			if (!file.exists()) {
				file.mkdir();
			}
			return file;
		}
		
		public static void main( String[] args )
		{
			staticFileLocation("/web-content");
			externalStaticFileLocation(Portal.getResourceFile().getPath())
			
			port(8080);
			get("/hello",{ req, res-> "Hello World"});
			
			File file = new File(Portal.getResourceFile(), "temp") ;
			FileUtils.deleteDirectory(file);
			file.mkdir();
			
			def loader = new GroovyClassLoader();
			
			def handler = {Request req ,Response res->
				res.type('application/json')
				
				try {
					def module = req.params('module').capitalize();
					println module;
					return loader.loadClass("musiclearning.service.${module}Service").newInstance().execute(req);
				} catch (Exception e) {
					res.status(500)
					return [error: e.message, type: e.class.name];
				}
			}
			
			def json={ model->
				return new JsonBuilder(model).toString()
			} as ResponseTransformer
		
			get('/sys/:module', handler, json);
			post('/sys/:module', handler, json);
		}
}
