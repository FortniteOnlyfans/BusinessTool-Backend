package dev.gr1.routes.calc;

import dev.gr1.proj.Calc;
import dev.gr1.routes.Utils;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class CalcVersionRouter implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (Utils.isExpired(request)) return Utils.expired();

            int id = Utils.intParam(request.params(":id"));
            if (id < 0) {
                return Utils.fail("Invalid version id");
            }

            Calc.Result result = Calc.calculateVersion(id);
            JSONObject payload = Calc.toJson(result);

            return Utils.success(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.fail(e.getMessage());
        }
    }
}
