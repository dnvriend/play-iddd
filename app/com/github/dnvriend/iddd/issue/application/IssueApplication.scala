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

package com.github.dnvriend.iddd.issue.application

import javax.inject.{ Inject, Singleton }

import akka.util.Timeout
import com.github.dnvriend.iddd.DomainEvent
import com.github.dnvriend.iddd.issue.domain.CreateIssueProduct
import com.github.dnvriend.iddd.issue.services.IssueProductService
import play.entity.registry.PersistentEntityRegistry
import scala.concurrent.{ ExecutionContext, Future }
import akka.pattern.ask

@Singleton
class IssueApplication @Inject() (registry: PersistentEntityRegistry)(implicit timeout: Timeout, ec: ExecutionContext) {
  def createIssueProduct(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String): Future[DomainEvent] = {
    for {
      ref <- registry.refFor[IssueProductService](productId)
      event <- (ref ? CreateIssueProduct(name, description, productManagerId, issueAssignerId, productId)).mapTo[DomainEvent]
    } yield event
  }
}
