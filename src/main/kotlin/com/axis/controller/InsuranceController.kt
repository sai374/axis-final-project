package com.axis.controller

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import io.swagger.annotations.ApiOperation
import org.bson.Document
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList


@RestController
class InsuranceController {

    @Autowired
    private val mongoTemplate: MongoTemplate? = null

    @Value("\${insurance.products}")
    private val product: Any? = null

    @Value("\${my.name}")
    private val name: Any? = null

    @ApiOperation(value = "Get all the products")
    @GetMapping("/products")
    //getting data from mongo
    fun getProducts(): ArrayList<*> {
        val products = ArrayList<Any>()
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("products")
            collection.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    doc.remove("_id")
                    if (products != null) {
                        products.add(doc)
                    }
                }
            }
        }
        return (products!!)
    }

    @ApiOperation(value = "Get form details for a particular product")
    @GetMapping("/forms/{insuranceProductId}")
    fun getFormsForProduct(@PathVariable insuranceProductId: String): ArrayList<*> {
        val formFields = ArrayList<Any>()
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("formFields")
            collection.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    doc.remove("_id")
                    if (doc.containsValue(insuranceProductId) && formFields != null) {
                        formFields.add(doc)
                    }
                }
            }
        }
        return (formFields!!)
    }

    @ApiOperation(value = "Get partners details for a particular product")
    @GetMapping("/partners/{insuranceProductId}")
    fun getPartnersForProduct(@PathVariable insuranceProductId: String): ArrayList<*> {
        val partners = ArrayList<Any>()
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("partners")
            collection.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    doc.remove("_id")
                    System.out.println(doc.get("productID").toString().contains(insuranceProductId))
                    if (doc.get("productID").toString().contains(insuranceProductId) && partners != null) {
                        partners.add(doc)
                    }
                }
            }
        }
        return (partners!!)
    }

    @ApiOperation(value = "Get quote urls from all partners for a product")
    @GetMapping("/quotes/{insuranceProductId}")
    fun getQuotesForProduct(@PathVariable insuranceProductId: String): ArrayList<Any> {
        val quotes = ArrayList<Any>()
        var endPoints = arrayListOf<String>()
        var endpoint: String = ""
        var product: String = ""
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection1: MongoCollection<org.bson.Document> = database.getCollection("products")
            collection1.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    doc.remove("_id")
                    if (doc.get("productID").toString().contains(insuranceProductId)) {
                        product = doc.get("id").toString()
                        break
                    }
                }
            }
            println(product)
            val collection2: MongoCollection<org.bson.Document> = database.getCollection("partners")
            collection2.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    doc.remove("_id")
                    endPoints = doc.get("apiEndpoints") as ArrayList<String>
                    for(endPoint in endPoints) {
                        if(endPoint.contains(product)) {
                            quotes.add(endPoint)
                        }
                    }
                }
            }
        }
        return (quotes!!)
    }

    @ApiOperation(value = "Get all car makers")
    @GetMapping("/car/makers")
    fun getCarMakers(): MutableSet<String> {
        var makers = mutableSetOf<String>()
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("carResource")
            collection.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    if(doc.get("Make").toString().length!=0)
                        makers.add(doc.get("Make") as String)
                }
            }
        }
        return (makers!!)
    }

    @ApiOperation(value = "Get all models of the car maker")
    @GetMapping("/car/models/{maker}")
    fun getCarModelsForMake(@PathVariable maker: String): MutableSet<String> {
        var models = mutableSetOf<String>()
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("carResource")
            collection.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    if(doc.get("Make")==maker) {
                        models.add(doc.get("Model") as String)
                    }
                }
            }
        }
        return (models!!)
    }

    @ApiOperation(value = "Get all variants of car model of car maker")
    @GetMapping("/car/variants/{maker}/{model}")
    fun getCarVariants(@PathVariable maker: String, @PathVariable model: String): MutableSet<String> {
        var variants = mutableSetOf<String>()
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("carResource")
            collection.find().iterator().use { cur ->
                while (cur.hasNext()) {
                    val doc = cur.next()
                    if(doc.get("Make")==maker && doc.get("Model")==model) {
                        variants.add(doc.get("Variant") as String)
                    }
                }
            }
        }
        return (variants!!)
    }

    @ApiOperation(value = "Add new products")
    @PostMapping("/products")
    fun addProducts(@RequestBody partner: String): Document {
        val parser = JSONParser()
        val jsonPartner = parser.parse(partner) as JSONObject
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("products")
            var doc: Document = Document.parse(jsonPartner.toString())
            collection.insertOne(doc)
            return doc
        }
    }

    @ApiOperation(value = "Add new partners")
    @PostMapping("/partners")
    fun addPartners(@RequestBody partner: String): Document {
        val parser = JSONParser()
        val jsonPartner = parser.parse(partner) as JSONObject
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("partners")
            var doc: Document = Document.parse(jsonPartner.toString())
            collection.insertOne(doc)
            return doc
        }
    }

    @ApiOperation(value = "Add form fields for a product")
    @PostMapping("/formFields")
    fun addFormFields(@RequestBody partner: String): Document {
        val parser = JSONParser()
        val jsonPartner = parser.parse(partner) as JSONObject
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("formFields")
            var doc: Document = Document.parse(jsonPartner.toString())
            collection.insertOne(doc)
            return doc
        }
    }

    @ApiOperation(value = "Add user")
    @PostMapping("/users")
    fun addUser(@RequestBody user: String): Document {
        val parser = JSONParser()
        val jsonPartner = parser.parse(user) as JSONObject
        MongoClients.create("mongodb://localhost:27017").use { mongoClient ->
            val database = mongoClient.getDatabase("axisdb")
            val collection: MongoCollection<org.bson.Document> = database.getCollection("users")
            var doc: Document = Document.parse(jsonPartner.toString())
            collection.insertOne(doc)
            return doc
        }
    }
}