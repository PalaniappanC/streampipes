package org.streampipes.storage.couchdb.impl;

import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.streampipes.model.client.connection.Connection;
import org.streampipes.model.client.pipeline.PipelineElementRecommendation;
import org.streampipes.storage.api.IPipelineElementConnectionStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConnectionStorageImpl extends AbstractDao<Connection> implements
        IPipelineElementConnectionStorage {


  public ConnectionStorageImpl() {
    super(Utils.getCouchDbConnectionClient(), Connection.class);
  }

  @Override
  public void addConnection(Connection connection) {
    persist(connection);
  }

  @Override
  public List<PipelineElementRecommendation> getRecommendedElements(String from) {
    // doesn't work as required object array is not created by lightcouch
    //List<JsonObject> obj = dbClient.view("connection/frequent").startKey(from).endKey(from, new Object()).group(true).query(JsonObject.class);
    String query;
    query = buildQuery(from);
    Optional<JsonObject> jsonObjectOpt = getFrequentConnections(query);
    if (jsonObjectOpt.isPresent()) {
      return handleResponse(jsonObjectOpt.get());
    } else {
      return Collections.emptyList();
    }

  }

  private String buildQuery(String from) {
    String escapedPath = UrlEscapers.urlPathSegmentEscaper().escape("startkey=[\"" + from + "\"]&endkey=[\"" + from + "\", {}]&group=true");
    return couchDbClient.getDBUri() + "_design/connection/_view/frequent?" + escapedPath;
  }

  private List<PipelineElementRecommendation> handleResponse(JsonObject jsonObject) {
    List<PipelineElementRecommendation> recommendations = new ArrayList<>();
    JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
    jsonArray.forEach(resultObj ->
            recommendations.add(makeRecommendation(resultObj)));
    return recommendations;
  }

  private PipelineElementRecommendation makeRecommendation(JsonElement resultObj) {
    PipelineElementRecommendation recommendation = new PipelineElementRecommendation();
    recommendation.setElementId(resultObj
            .getAsJsonObject()
            .get("key")
            .getAsJsonArray()
            .get(1).getAsString());

    recommendation.setCount(resultObj
            .getAsJsonObject()
            .get("value")
            .getAsInt());

    return recommendation;
  }

  private Optional<JsonObject> getFrequentConnections(String query) {
    try {
      return Optional.of((JsonObject) new JsonParser().parse(Request.Get(query).execute().returnContent().asString()));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

}