package musiclearning.portal

import static spark.Spark.*;
import groovy.json.JsonBuilder

import java.rmi.server.LoaderHandler;

import org.apache.commons.io.FileUtils;

import spark.Request;
import spark.Response;
import spark.ResponseTransformer;

class Portal {
		
		public static void main( String[] args )
		{
			staticFileLocation("/web-content");
			
			port(8080);
			get("/hello",{ req, res-> "Hello World"});
			
			File file = new File("src/main/resources/web-content/temp/");
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
