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

package com.github.dnvriend.iddd.agile.domain.backlogitem

import com.github.dnvriend.iddd.{ DomainCommand, DomainEvent }
import com.github.dnvriend.iddd.agile.domain.product.ProductId

import scala.compat.Platform

final case class Task(priority: Int, description: String)
final case class BacklogItemId(id: String)

// command
final case class AddBacklogItem(productId: String, backlogItemId: String, description: String) extends DomainCommand
final case class DefineTaskToBacklogItem(backlogItemId: String, taskDescription: String, taskPriority: Int) extends DomainCommand

// event
final case class BacklogItemAddedToProduct(productId: String, backlogItemId: String, description: String, occurredOn: Long, eventVersion: Long) extends DomainEvent
final case class TaskDefinedForBacklogItem(backlogItemId: String, taskDescription: String, taskPriority: Int, occurredOn: Long, eventVersion: Long) extends DomainEvent

// domain
final case class BacklogItem(backlogItemId: BacklogItemId, productId: ProductId, description: String, tasks: List[Task])

object BacklogItem {
  def handleCommand(state: Option[BacklogItem], latestVersion: Long, cmd: DomainCommand): (Option[BacklogItem], DomainEvent) = cmd match {
    case AddBacklogItem(productId, backlogItemId, description) =>
      val state = BacklogItem(BacklogItemId(backlogItemId), ProductId(productId), description, List.empty[Task])
      val event = BacklogItemAddedToProduct(productId, backlogItemId, description, Platform.currentTime, latestVersion + 1)
      (Option(state), event)
  }

  //  def handleEvent()
}
