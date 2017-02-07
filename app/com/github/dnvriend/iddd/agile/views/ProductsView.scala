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

package com.github.dnvriend.iddd.agile.views

import akka.actor.{ Actor, ActorLogging }
import com.github.dnvriend.iddd.DomainEvent
import com.github.dnvriend.iddd.agile.domain.product.AgileProductCreated
import com.github.dnvriend.iddd.agile.views.ProductsView.{ GetProductsRequest, GetProductsResponse }

object ProductsView {
  final case class Product(productId: String, name: String, description: String, occurredOn: Long, eventVersion: Long)

  // cmd
  case object GetProductsRequest
  case class GetProductsResponse(xs: List[Product])
}

class ProductsView extends Actor with ActorLogging {
  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[DomainEvent])
  }
  override def receive: Receive = products(List.empty[ProductsView.Product])

  def products(xs: List[ProductsView.Product]): Receive = {
    case AgileProductCreated(name, description, productManagerId, issueAssignerId, productId, occurredOn, eventVersion) =>
      val product = ProductsView.Product(productId, name, description, occurredOn, eventVersion)
      log.debug("Adding product: {}", product)
      context.become(products(xs :+ product))

    case GetProductsRequest =>
      sender() ! GetProductsResponse(xs)
  }
}
