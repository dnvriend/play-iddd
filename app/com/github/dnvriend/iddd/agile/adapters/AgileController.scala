/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.iddd.agile.adapters

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorRef
import com.google.inject.name.Named
import play.api.libs.json.Json
import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout
import com.github.dnvriend.iddd.agile.application.AgileApplication
import com.github.dnvriend.iddd.agile.views.ProductsView

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

final case class CreateProductDto(name: String, description: String, productManagerId: String, issueAssignerId: String)
object CreateProductDto {
  implicit val format = Json.format[CreateProductDto]
}

final case class ProductDto(productId: String, name: String, description: String)
object ProductDto {
  implicit val format = Json.format[ProductDto]
  def productToProductDto(p: ProductsView.Product): ProductDto =
    ProductDto(p.productId, p.name, p.description)
}

class AgileController @Inject() (agileApplication: AgileApplication, @Named("products-view") productsView: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) extends Controller {
  // http :9000/api/products name=foo description=desc productManagerId=1 issueAssignerId=1
  def createProduct = Action.async { implicit request =>
    val productId = UUID.randomUUID.toString
    for {
      cmd <- Future.fromTry(Try(request.body.asJson.map(_.as[CreateProductDto]).get))
      _ <- agileApplication.createProduct(cmd.name, cmd.description, cmd.productManagerId, cmd.issueAssignerId, productId)
    } yield Ok(productId)
  }

  // http :9000/api/products
  def getProducts = Action.async {
    for {
      response <- (productsView ? ProductsView.GetProductsRequest).mapTo[ProductsView.GetProductsResponse]
      listOfProductDto = response.xs.map(ProductDto.productToProductDto)
    } yield Ok(Json.toJson(listOfProductDto))
  }
}
