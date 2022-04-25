package org.apache.james.gatling.imap.scenari

import java.util.Calendar

import com.linagora.gatling.imap.PreDef._
import com.linagora.gatling.imap.protocol.command.FetchAttributes.AttributeList
import com.linagora.gatling.imap.protocol.command.MessageRange.Last
import com.linagora.gatling.imap.protocol.command.MessageRanges
import io.gatling.core.Predef._

object ImapCommonSteps {
  val receiveEmail = exec(imap("append").append("INBOX", Some(scala.collection.immutable.Seq("\\Flagged")), Option.empty[Calendar],
    """Return-Path: <expeditor@linagora.com>
      |Delivered-To: recipient@linagora.com
      |Received: from 172.17.0.1 (EHLO incoming.linagora.com) ([172.17.0.1])
      |          by incoming.linagora.com (JAMES SMTP Server ) with ESMTP ID 3544dff2;
      |          Mon, 25 Apr 2022 04:57:12 +0000 (UTC)
      |Received: from smtp.linagora.com (smtp.linagora.com [172.17.0.1])
      |	by incoming.linagora.com (Postfix) with ESMTPS id BDCF99C621;
      |	Mon, 25 Apr 2022 04:57:12 +0000 (UTC)
      |Received: from ?Open?PaaS?SMTP?server?for?Linagora? (unknown [172.17.0.1])
      |	(using TLSv1.2 with cipher ECDHE-RSA-AES256-GCM-SHA384 (256/256 bits))
      |	(No client certificate requested)
      |	by smtp.linagora.com (Postfix) with ESMTPSA id B0A8A4051D;
      |	Mon, 25 Apr 2022 06:57:12 +0200 (CEST)
      |DKIM-Signature: a=rsa-sha256; b=KI/5QjJgj7jTIsvGgpYaGoMj75nCrIAqcLevpjMjzHKqtjrKAG2TULkGZ1DvfcdguB2SK4fOB6pmYn+UC2DcOTaAAjRNX1rDDj0Q0lU8u4P45U+muP9qlGjya3LgrGrHlYjYAup6gHPMdJQXe59hQ0rUleZah+b0IsioyEgiI6F9XQREssjGw8V0gWwJgYmh3AClUIMYczZsNT87v5houHKHUh06Q603kRTuGN6RJPBOoJEG6je//SP8pk0Ay9qb0g9oqKzSUF4qgRWpEUBNB0PBX3DIBvFdJqDu7GdnRjQTnfMLTBzPU/kP75Nj67lsB0PRtrfdt6pT8b2sp3HLBQ==; s=smtpoutjames; d=linagora.com; v=1; bh=DhewKt8OscsMcFnYu6bXGkWxf5BF0S+D39afS8dfMtY=; h=from : reply-to : subject : date : to : cc : resent-date : resent-from : resent-sender : resent-to : resent-cc : in-reply-to : references : list-id : list-help : list-unsubscribe : list-subscribe : list-post : list-owner : list-archive;
      |MIME-Version: 1.0
      |Content-Type: text/plan; charset=UTF-8
      |Content-Transfer-Encoding: quoted-printable
      |X-LINAGORA-Copy-Delivery-Done: 1
      |From: Expeditor <expeditor@linagora.com>
      |Sender: Expeditor <expeditor@linagora.com>
      |Reply-To: expeditor@linagora.com
      |To: Recipient <recipient@linagora.com>
      |Cc: Recipient2 <recipient2@linagora.com>,
      |Subject: test subject
      |Message-ID: <Mime4j.343.23a1d0ac2e082f07.1805f14ac19@linagora.com>
      |Date: Mon, 25 Apr 2022 04:57:11 +0000
      |
      |Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam efficitur posuere elit, sed consequat elit
      |facilisis in. Etiam efficitur sagittis consequat. Nunc a quam feugiat diam tincidunt imperdiet id finibus
      |ante. Pellentesque a lobortis ex. Etiam a arcu quam. Sed sodales iaculis mauris at euismod. Sed nec sapien
      |congue, faucibus tortor at, tincidunt nisi. Duis a tristique arcu, faucibus iaculis mi. Vestibulum efficitur
      |nibh non purus vestibulum, ac luctus sem iaculis. Nunc vel metus eu neque porttitor mollis vitae ut justo.
      |Cras vehicula sem felis, in auctor mi eleifend sed.
      |
      |Integer vel tortor eu turpis fringilla eleifend. Cras faucibus leo eget lobortis iaculis. Etiam eros felis,
      |vulputate non lacinia eu, consequat sit amet dolor. Maecenas porta purus magna, at ultrices velit tempor eget.
      |Cras sollicitudin dolor eu volutpat posuere. Sed dictum ex eget odio posuere consectetur. Class aptent taciti
      |sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce malesuada suscipit nisl vel
      |gravida. Maecenas pulvinar, velit ut viverra pellentesque, dui ante elementum diam, et eleifend erat eros
      |sed massa. Quisque eget est sed ex dictum maximus. Aenean lacus justo, pellentesque ac placerat eget, cursus
      |ac urna.
      |
      |Curabitur pellentesque augue eu pharetra aliquam. Nulla pretium risus non tortor suscipit porta. Cras
      |pulvinar, justo quis consectetur aliquam, turpis erat facilisis nulla, nec lacinia turpis purus et nunc.
      |Nunc eget nibh nibh. Nam euismod, arcu in suscipit auctor, risus est luctus quam, at ultricies orci lectus
      |ac ligula. Fusce vel est hendrerit, varius velit et, hendrerit mi. Duis tortor magna, varius vitae commodo
      |et, varius at lacus. Suspendisse eget pharetra odio, quis pulvinar metus. Sed ut erat ipsum. Sed suscipit
      |eleifend tempor. Cras leo urna, semper ut nibh et, mollis tincidunt dui. Phasellus tempus congue enim
      |rhoncus faucibus. Curabitur vel enim in quam fringilla tristique sed ut neque. In lacus elit, efficitur
      |ut nibh quis, tempus auctor felis. Nullam finibus nisi vestibulum lacinia venenatis.
      |""".stripMargin).check(ok))

  val readLastEmail = exec(imap("list").list("", "*").check(ok, hasFolder("INBOX")))
    .exec(imap("select").select("INBOX").check(ok))
    .exec(imap("fetch").fetch(MessageRanges(Last()), AttributeList("UID", "BODY[HEADER]", "BODY[TEXT]")).check(ok))
}
