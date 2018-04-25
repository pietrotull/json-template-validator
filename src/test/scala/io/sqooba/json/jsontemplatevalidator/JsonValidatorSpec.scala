package io.sqooba.json.jsontemplatevalidator

import org.scalatest._

class JsonValidatorSpec extends FlatSpec with Matchers {

  val json1 = """{ "id": 5, "desc":"someval" }"""
  val json2 = """{ "id": 5, "desc":"someval", "other":"optional" }"""
  val json3 = """{ "id": 1, "desc":"other", "otherother":"optional" }"""

  val invalidJson1 = """{ "desc":"someval", "other":"optional" }"""
  val invalidJson2 = """{ "other":"optional" }"""

  val nestedJson1 = """{ "id": 5, "desc":"someval", "tree": { "nested1":"nestedval" } }"""
  val nestedJson2 = """{ "id": 5, "desc":"someval", "tree": { "nested1":"nestedval", "nested2":"nestedval" } }"""
  val invalidNestedJson = """{ "id": 5, "desc":"someval", "tree": { "nested2":"nestedval" } }"""


  "JsonValidator" should "create a validator based on json" in {
    JsonValidator.forTemplateJson(json1) shouldBe a [JsonValidator]
  }

  "JsonValidator" should "validate legal json" in {
    val validator = JsonValidator.forTemplateJson(json1)
    validator.validateJson(json1) shouldBe true
    validator.validateJson(json2) shouldBe true
    validator.validateJson(json3) shouldBe true
  }

  "JsonValidator" should "reject json with missing fields" in {
    val validator = JsonValidator.forTemplateJson(json1)
    validator.validateJson(invalidJson1) shouldBe false
    validator.validateJson(invalidJson2) shouldBe false
  }

  "NestedValidator" should "validate also nested values" in {
    val nestedValidator = JsonValidator.forTemplateJson(nestedJson1)
    nestedValidator shouldBe a [JsonValidator]
    nestedValidator.validateJson(nestedJson1) shouldBe true
    nestedValidator.validateJson(nestedJson2) shouldBe true
    nestedValidator.validateJson(invalidNestedJson) shouldBe false
  }

  // invalid json is false
}