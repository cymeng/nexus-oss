/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.integrationtests.nexus393;

import javax.mail.internet.MimeMessage;

import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractEmailServerNexusIT;
import org.sonatype.nexus.test.utils.ResetPasswordUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.icegreen.greenmail.util.GreenMailUtil;


/**
 * Test password reset.  Check if nexus is sending the e-mail. 
 */
public class Nexus393ResetPasswordIT
    extends AbstractEmailServerNexusIT
{

    @Test
    public void resetPassword()
        throws Exception
    {
        String username = "test-user";
        Response response = ResetPasswordUtils.resetPassword( username );
        Assert.assertTrue( response.getStatus().isSuccess(), "Status: "+ response.getStatus() +"\n"+ response.getEntity().getText() );

        // Need 1 message
        waitForMail( 1 );

        MimeMessage[] msgs = server.getReceivedMessages();
        Assert.assertTrue( msgs != null && msgs.length > 0, "Expected email." );
        MimeMessage msg = msgs[0];

        String password = null;
        // Sample body: Your password has been reset. Your new password is: c1r6g4p8l7
        String body = GreenMailUtil.getBody( msg );
        
        int index = body.indexOf( "Your new password is: " );
        int passwordStartIndex = index + "Your new password is: ".length();
        if ( index != -1 )
        {
            password = body.substring( passwordStartIndex, body.indexOf( '\n', passwordStartIndex ) ).trim();
            log.debug( "New password:\n" + password );
        }

        Assert.assertNotNull( password );
    }

}
