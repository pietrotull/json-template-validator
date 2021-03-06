package io.sqooba.json.jsontemplatevalidator

import org.scalatest._

class JsonValidatorSpec extends FlatSpec with Matchers {

  val json1 = """{ "id": 5, "desc":"someval" }"""
  val json2 = """{ "id": 5, "desc":"someval", "other":"optional" }"""
  val json3 = """{ "id": 1, "desc":"other", "otherother":"optional" }"""
  val arrayJson = """{ "id": 1, "arrayVal": ["string"] }"""

  val jsonWithJsonInArray = """{ "id": 1, "arrayVal": [{"key":"key", "value":"value"}] }"""

  val jsonWithEmptyArray = """{ "id": 1, "arrayVal": [] }"""

  val invalidArrayJson = """{ "id": 1, "arrayVal": "string" }"""

  val nonJson = "someother string:{id:[],}"

  val invalidJson1 = """{ "desc":"someval", "other":"optional" }"""
  val invalidJson2 = """{ "other":"optional" }"""

  val nestedJson1 = """{ "id": 5, "desc":"someval", "tree": { "nested1":"nestedval" } }"""
  val nestedJson2 = """{ "id": 5, "desc":"someval", "tree": { "nested1":"nestedval", "nested2":"nestedval" } }"""
  val invalidNestedJson = """{ "id": 5, "desc":"someval", "tree": { "nested2":"nestedval" } }"""
  val simpleArray = """ [{"key":"key", "value":"value"}] """
  val simpleWrongArray = """ [{"key":"key", "value":"value"}, { "perse" : "arse" } ] """

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

  "Invalid json format" should "return false" in {
    val json ="{baa:'b;a'}"
    assertThrows[IllegalArgumentException] {
      val validator = JsonValidator.forTemplateJson(json)
    }
  }

  "JsonValidator" should "return false for invalid json format" in {
    val validator = JsonValidator.forTemplateJson(json1)
    validator.validateJson(nonJson) shouldBe false
  }

  "JsonValidator" should "return true for valid array" in {
    val validator = JsonValidator.forTemplateJson(arrayJson)
    validator.validateJson(arrayJson) shouldBe true
  }

  "JsonValidator" should "return false for field that is not an array" in {
    val validator = JsonValidator.forTemplateJson(arrayJson)
    validator.validateJson(invalidArrayJson) shouldBe false
  }

  "JsonValidator" should "check items inside array aswell" in {
    val validator = JsonValidator.forTemplateJson(jsonWithJsonInArray)
    validator.validateJson(arrayJson) shouldBe false
  }

  "JsonValidator" should "check empty array" in {
    val validator = JsonValidator.forTemplateJson("[]")
    validator.validateJson("[]") shouldBe true
  }

  "JsonValidator" should "check items inside array as well" in {
    val validator = JsonValidator.forTemplateJson(simpleArray)
    validator.validateJson(simpleArray) shouldBe true
  }

  "JsonValidator" should "check for inconsistent items inside array" in {
    val validator = JsonValidator.forTemplateJson(simpleArray)
    validator.validateJson(simpleWrongArray) shouldBe false
  }

  "JsonValidator" should "reject empty array" in {
    val validator = JsonValidator.forTemplateJson(simpleArray)
    validator.validateJson("[]") shouldBe false
  }

  "JsonValidator" should "check items inside array as well, reject empty array" in {
    val validator = JsonValidator.forTemplateJson(jsonWithJsonInArray)
    validator.validateJson(jsonWithEmptyArray) shouldBe false
  }

  "JsonValidator" should "check item is an array as well, accept empty array" in {
    val validator = JsonValidator.forTemplateJson(jsonWithEmptyArray)
    validator.validateJson(jsonWithEmptyArray) shouldBe true
  }
}
