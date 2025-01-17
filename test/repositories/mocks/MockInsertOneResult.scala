/*
 * Copyright 2022 HM Revenue & Customs
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

package repositories.mocks

import org.mockito.Mockito.{reset, verify, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatestplus.mockito.MockitoSugar
import org.mongodb.scala.result.InsertOneResult

trait MockInsertOneResult extends MockitoSugar with BeforeAndAfterEach {
  self: Suite =>

  val mockInsertOneResult: InsertOneResult = mock[InsertOneResult]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockInsertOneResult)
  }

  def mockWasAcknowledged()(response: Boolean): OngoingStubbing[Boolean] =
    when(mockInsertOneResult.wasAcknowledged()).thenReturn(response)

  def verifyWasAcknowledged(): Unit = verify(mockInsertOneResult).wasAcknowledged()
}
