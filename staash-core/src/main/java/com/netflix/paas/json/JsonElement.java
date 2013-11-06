package com.netflix.paas.json;

public abstract class JsonElement {
  public boolean isArray() {
    return this instanceof JsonArray;
  }

  public boolean isObject() {
    return this instanceof JsonObject;
  }

  public JsonArray asArray() {
    return (JsonArray) this;
  }

  public JsonObject asObject() {
    return (JsonObject) this;
  }
}