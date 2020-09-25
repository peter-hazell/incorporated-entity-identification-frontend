/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.incorporatedentityidentificationfrontend.views.errorpages

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.incorporatedentityidentificationfrontend.assets.MessageLookup.{Base, CompanyNumberNotFound => messages}
import uk.gov.hmrc.incorporatedentityidentificationfrontend.utils.ViewSpecHelper._

trait CompanyNumberNotFoundTests {
  this: AnyWordSpec with Matchers =>

  def testCompanyNumberNotFoundView(result: => WSResponse, authStub: => StubMapping): Unit = {
    lazy val doc: Document = {
      authStub
      Jsoup.parse(result.body)
    }

    "have the correct title" in {
      doc.title mustBe messages.title
    }

    "have the correct heading" in {
      doc.getH1Elements.first.text mustBe messages.heading
    }

    "have the correct paragraph" in {
      doc.getParagraphs.first.text mustBe messages.line
    }

    "have a try again button" in {
      doc.getSubmitButton.first.text mustBe Base.tryAgain
    }
  }

}