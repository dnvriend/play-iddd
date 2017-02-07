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

package com.github.dnvriend.iddd.issue.adapters

import akka.actor.{ Actor, ActorLogging, Stash, Status }
import com.github.dnvriend.iddd.DomainEvent
import com.github.dnvriend.iddd.agile.domain.product.AgileProductCreated
import com.github.dnvriend.iddd.issue.application.IssueApplication
import akka.pattern.pipe

import scala.concurrent.ExecutionContext

class AgileProductCreatedAdapter(issueApplication: IssueApplication)(implicit ec: ExecutionContext) extends Actor with ActorLogging with Stash {
  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[DomainEvent])
  }

  def disabled: Receive = {
    case Status.Success(result) =>
      log.debug("Success calling IssueApplication: {}", result)
      unstashAll()
      context.become(receive)
    case Status.Failure(t) =>
      log.error(t, "Error while calling IssueApplication")
      unstashAll()
      context.become(receive)
    case _ =>
      stash()
  }

  override def receive: Receive = {
    case AgileProductCreated(name, description, productManagerId, issueAssignerId, productId, timestamp, eventVersion) =>
      issueApplication.createIssueProduct(name, description, productManagerId, issueAssignerId, productId)
        .map(result => Status.Success(result))
        .pipeTo(self)

      context.become(disabled)
  }
}
