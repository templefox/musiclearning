package musiclearning.service

import spark.Request;
import spark.Response;

trait Service {
	abstract def execute(Request req)
}
