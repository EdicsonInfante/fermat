package integration.com.bitdubai.fermat_p2p_plugin.layer._11_communication.cloud_server.developer.bitdubai.version_1.cloud.CloudNetworkServiceManager;

import static org.fest.assertions.api.Assertions.*;
import integration.com.bitdubai.fermat_p2p_plugin.layer._11_communication.cloud_server.developer.bitdubai.version_1.cloud.mocks.MockFMPPacketsFactory;

import org.junit.Before;
import org.junit.Test;

import com.bitdubai.fermat_api.layer._10_communication.fmp.FMPPacket;
import com.bitdubai.fermat_api.layer._10_communication.fmp.FMPPacket.FMPPacketType;
import com.bitdubai.fermat_api.layer._1_definition.crypto.asymmetric.AsymmectricCryptography;

/**
 * Created by jorgeejgonzalez on 27/04/15.
 */
public class ConnectionRequestTest extends CloudNetworkServiceManagerIntegrationTest {
	
	
	@Before
	public void setUpParameters() throws Exception{
		setUpAddressInfo(TCP_BASE_TEST_PORT+200);
		setUpKeyPair();
		setUpExecutor(2);		
	}
	
	@Test
	public void ConnectionRequest_SendValidRequest_ClientGetsResponse() throws Exception{
		setUpConnections(0);
		FMPPacket request = MockFMPPacketsFactory.mockRequestIntraUserNetworkServicePacket(testManager.getPublicKey());
		testClient.sendMessage(request);
		FMPPacket response = getResponse();
		assertThat(response).isNotNull();
	}
	
	@Test
	public void ConnectionRequest_SendValidRequest_ResponseTypeConnectionAccept() throws Exception{
		setUpConnections(1);
		FMPPacket request = MockFMPPacketsFactory.mockRequestIntraUserNetworkServicePacket(testManager.getPublicKey());
		testClient.sendMessage(request);				
		FMPPacket response = getResponse();
		assertThat(response.getType()).isEqualTo(FMPPacketType.CONNECTION_ACCEPT);
	}
	
	@Test
	public void ConnectionRequest_SendValidRequest_ResponseDestinationEqualsRequestSender() throws Exception{
		setUpConnections(2);
		FMPPacket request = MockFMPPacketsFactory.mockRequestIntraUserNetworkServicePacket(testManager.getPublicKey());
		testClient.sendMessage(request);
		FMPPacket response = getResponse();
		assertThat(response.getDestination()).isEqualTo(request.getSender());
	}
	
	@Test
	public void ConnectionRequest_SendValidRequest_ResponseSignatureVerified() throws Exception{
		setUpConnections(3);
		FMPPacket request = MockFMPPacketsFactory.mockRequestIntraUserNetworkServicePacket(testManager.getPublicKey());
		testClient.sendMessage(request);		
		FMPPacket response = getResponse();		
		boolean signatureVerification = AsymmectricCryptography.verifyMessageSignature(response.getSignature(), response.getMessage(), response.getSender());
		assertThat(signatureVerification).isTrue();	
	}
	
	@Test
	public void ConnectionRequest_SendRequestForDifferentNetworkService_ResponseSignatureVerified() throws Exception{
		setUpConnections(4);
		FMPPacket request = MockFMPPacketsFactory.mockRequestMoneyNetworkServicePacket(testManager.getPublicKey());
		testClient.sendMessage(request);		
		FMPPacket response = getResponse();		
		assertThat(response.getType()).isEqualTo(FMPPacketType.CONNECTION_DENY);
	}
	
	@Test
	public void ConnectionRequest_RequestDestinationInvalid_NoResponse() throws Exception{
		setUpConnections(5);
		FMPPacket request = MockFMPPacketsFactory.mockRequestIntraUserNetworkServicePacket();
		testClient.sendMessage(request);
		FMPPacket response = getResponse();
		assertThat(response).isNull();
	}

}