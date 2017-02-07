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

package com.github.dnvriend.iddd.issue.domain

import com.github.dnvriend.iddd.{ DomainCommand, DomainEvent }

import scala.compat.Platform

// cmd
final case class CreateIssueProduct(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String) extends DomainCommand

// event
final case class IssueProductCreated(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String, occurredOn: Long, eventVersion: Long) extends DomainEvent

// domain
final case class IssueProduct(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String)

object IssueProduct {
  def handleCommand(state: Option[IssueProduct], lastSequenceNr: Long, cmd: DomainCommand): (Option[IssueProduct], DomainEvent) = cmd match {
    case CreateIssueProduct(name, description, productManagerId, issueAssignerId, productId) =>
      val state = IssueProduct(name, description, productManagerId, issueAssignerId, productId)
      val event = IssueProductCreated(name, description, productManagerId, issueAssignerId, productId, Platform.currentTime, lastSequenceNr + 1)
      (Option(state), event)
  }

  def handleEvent(state: Option[IssueProduct], event: DomainEvent): Option[IssueProduct] = event match {
    case IssueProductCreated(name, description, productManagerId, issueAssignerId, productId, timestamp, eventVersionN) =>
      Option(IssueProduct(name, description, productManagerId, issueAssignerId, productId))
  }
}