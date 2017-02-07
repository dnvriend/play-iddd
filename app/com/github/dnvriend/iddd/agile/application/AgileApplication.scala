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

package com.github.dnvriend.iddd.agile.application

import javax.inject.{ Inject, Singleton }

import akka.pattern.ask
import akka.util.Timeout
import com.github.dnvriend.iddd.DomainEvent
import com.github.dnvriend.iddd.agile.domain.product.CreateAgileProduct
import com.github.dnvriend.iddd.agile.services.AgileProductService
import play.entity.registry.PersistentEntityRegistry

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class AgileApplication @Inject() (registry: PersistentEntityRegistry)(implicit timeout: Timeout, ec: ExecutionContext) {
  def createProduct(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String): Future[DomainEvent] = for {
    ref <- registry.refFor[AgileProductService](productId)
    event <- (ref ? CreateAgileProduct(name, description, productManagerId, issueAssignerId, productId)).mapTo[DomainEvent]
  } yield event

}
