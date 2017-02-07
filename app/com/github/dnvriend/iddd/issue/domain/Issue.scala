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
import com.github.dnvriend.iddd.agile.domain.product.ProductId
import scala.compat.Platform

// cmd
final case class AdjustSeverity(issueSeverity: Severity) extends DomainCommand
final case class AssignIssueToExistingBacklogItem(backlogItemId: String) extends DomainCommand
final case class AttachAssignedBacklogItemId(backlogItemId: String) extends DomainCommand

// events
final case class IssueAlreadyAssignedToExistingBacklogItem(backlogItemId: String, issueId: IssueId, issueType: IssueType, productId: ProductId, occurredOn: Long, eventVersion: Long) extends DomainEvent
final case class IssueAssignedToExistingBacklogItem(backlogItemId: String, issueId: IssueId, issueType: IssueType, productId: ProductId, occurredOn: Long, eventVersion: Long) extends DomainEvent
final case class IssueAssignedToNewBacklogItem(description: String, issueId: IssueId, issueType: IssueType, productId: ProductId, summary: String, occurredOn: Long, eventVersion: Long) extends DomainEvent
final case class IssueReported(description: String, issueId: IssueId, productId: ProductId, severity: Severity, summary: String, `type`: IssueType, occurredOn: Long, eventVersion: Long) extends DomainEvent
final case class IssueSeverityAdjusted(currentSeverity: Severity, issueId: IssueId, previousSeverity: Severity, productId: ProductId, occurredOn: Long, eventVersion: Long) extends DomainEvent

// id
final case class IssueId(id: String)

// domain
final case class Issue(
    assignedBacklogItemId: Option[String],
    description: String,
    issueId: IssueId,
    productId: ProductId,
    severity: Severity,
    summary: String,
    `type`: IssueType
) {

  def isDefect: Boolean = `type` == IssueType.Defect

  def isFeatureRequest: Boolean = `type` == IssueType.FeatureRequest

  def isHighSeverity: Boolean = severity == Severity.High

  def isMediumSeverity: Boolean = severity == Severity.Medium

  def isLowSeverity: Boolean = severity == Severity.Low
}

object Issue {
  def handleCommand(state: Option[Issue], lastSequenceNr: Long, cmd: DomainCommand): (Option[Issue], DomainEvent) = ???

  def handleEvent(state: Option[Issue], event: DomainEvent): Option[Issue] = ???
}

//def adjustSeverity(issueSeverity: Severity): (DomainEvent, Issue) =
//(IssueSeverityAdjusted(issueSeverity, 1, issueId, severity, productId, Platform.currentTime), copy(severity = issueSeverity))

//def assignIssueToExistingBacklogItem(backlogItemId: String): (DomainEvent, Issue) =
//if (isAlreadyAssignedToBacklogItemId(backlogItemId))
//(IssueAlreadyAssignedToExistingBacklogItem(backlogItemId, 1, issueId, `type`, productId, Platform.currentTime), this)
//else
//(IssueAssignedToExistingBacklogItem(backlogItemId, 1, issueId, `type`, productId, Platform.currentTime), copy(assignedBacklogItemId = Option(backlogItemId)))

//def isAlreadyAssignedToBacklogItemId(backlogItemId: String): Boolean =
//assignedBacklogItemId.contains(backlogItemId)

//def attachAssignedBacklogItemId(backlogItemId: String): (DomainEvent, Issue) =
//if (isAlreadyAssignedToBacklogItemId(backlogItemId))
//(IssueAlreadyAssignedToExistingBacklogItem(backlogItemId, 1, issueId, `type`, productId, Platform.currentTime), this)
//else
//(IssueAssignedToNewBacklogItem(description, 1, issueId, `type`, productId, summary, Platform.currentTime), copy(assignedBacklogItemId = Option(backlogItemId)))

