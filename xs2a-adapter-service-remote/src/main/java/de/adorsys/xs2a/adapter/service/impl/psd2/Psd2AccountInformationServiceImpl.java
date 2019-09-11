package de.adorsys.xs2a.adapter.service.impl.psd2;

import de.adorsys.xs2a.adapter.api.remote.psd2.Psd2AccountInformationClient;
import de.adorsys.xs2a.adapter.mapper.psd2.Psd2AccountInformationMapper;
import de.adorsys.xs2a.adapter.rest.psd2.model.*;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.mapper.ResponseHeadersMapper;
import de.adorsys.xs2a.adapter.service.psd2.Psd2AccountInformationService;
import de.adorsys.xs2a.adapter.service.psd2.model.*;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public class Psd2AccountInformationServiceImpl implements Psd2AccountInformationService {

    private final Psd2AccountInformationClient client;
    private final Psd2AccountInformationMapper accountInformationMapper = Mappers.getMapper(Psd2AccountInformationMapper.class);
    private final ResponseHeadersMapper responseHeadersMapper = Mappers.getMapper(ResponseHeadersMapper.class);

    public Psd2AccountInformationServiceImpl(Psd2AccountInformationClient client) {
        this.client = client;
    }

    @Override
    public Response<AccountList> getAccounts(Map<String, String> queryParameters, Map<String, String> headers) throws IOException {
        ResponseEntity<AccountListTO> responseEntity = client.getAccountList(queryParameters, headers);
        AccountList accountList = accountInformationMapper.toAccountList(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            accountList,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<ReadAccountBalanceResponse> getBalances(String accountId, Map<String, String> queryParameters, Map<String, String> headers) throws IOException {
        ResponseEntity<ReadAccountBalanceResponseTO> responseEntity = client.getBalances(accountId, queryParameters, headers);
        ReadAccountBalanceResponse readAccountBalanceResponse = accountInformationMapper.toReadAccountBalanceResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            readAccountBalanceResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<?> getTransactions(String accountId, Map<String, String> queryParameters, Map<String, String> headers) throws IOException {
        ResponseEntity<?> responseEntity = client.getTransactionList(accountId, queryParameters, headers);

        Object responseBody = responseEntity.getBody();

        if (responseBody instanceof TransactionsResponseTO) {
            TransactionsResponse transactionsResponse = accountInformationMapper.toTransactionsResponse((TransactionsResponseTO) responseBody);
            return new Response<>(
                responseEntity.getStatusCodeValue(),
                transactionsResponse,
                responseHeadersMapper.getHeaders(responseEntity.getHeaders())
            );
        }

        return new Response<>(
            responseEntity.getStatusCodeValue(),
            responseBody,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<ConsentsResponse> createConsent(Map<String, String> headers, Consents consents) {
        ResponseEntity<ConsentsResponseTO> responseEntity = client.createConsent(headers, accountInformationMapper.toConsentsTO(consents));
        ConsentsResponse consentsResponse = accountInformationMapper.toConsentsResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            consentsResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<ConsentInformationResponse> getConsentInformation(String consentId, Map<String, String> headers) {
        ResponseEntity<ConsentInformationResponseTO> responseEntity = client.getConsentInformation(consentId, headers);
        ConsentInformationResponse consentInformationResponse = accountInformationMapper.toConsentInformationResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            consentInformationResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<Void> deleteConsent(String consentId, Map<String, String> headers) {
        ResponseEntity<Void> responseEntity = client.deleteConsent(consentId, headers);
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            null,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<ConsentStatusResponse> getConsentStatus(String consentId, Map<String, String> headers) {
        ResponseEntity<ConsentStatusResponseTO> responseEntity = client.getConsentStatus(consentId, headers);
        ConsentStatusResponse consentStatusResponse = accountInformationMapper.toConsentStatusResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            consentStatusResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<StartScaProcessResponse> startConsentAuthorisation(String consentId, Map<String, String> headers, UpdateAuthorisation updateAuthentication) {
        ResponseEntity<StartScaProcessResponseTO> responseEntity = client.startConsentAuthorisation(consentId, headers, accountInformationMapper.toUpdateAuthorisationTO(updateAuthentication));
        StartScaProcessResponse startScaProcessResponse = accountInformationMapper.toStartScaProcessResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            startScaProcessResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<UpdateAuthorisationResponse> updateConsentsPsuData(String consentId, String authorisationId, Map<String, String> headers, UpdateAuthorisation updateAuthentication) {
        ResponseEntity<UpdateAuthorisationResponseTO> responseEntity = client.updateConsentsPsuData(consentId, authorisationId, headers, accountInformationMapper.toUpdateAuthorisationTO(updateAuthentication));
        UpdateAuthorisationResponse updateAuthorisationResponse = accountInformationMapper.toUpdateAuthorisationResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            updateAuthorisationResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }

    @Override
    public Response<ScaStatusResponse> getConsentScaStatus(String consentId, String authorisationId, Map<String, String> headers) {
        ResponseEntity<ScaStatusResponseTO> responseEntity = client.getConsentScaStatus(consentId, authorisationId, headers);
        ScaStatusResponse scaStatusResponse = accountInformationMapper.toScaStatusResponse(responseEntity.getBody());
        return new Response<>(
            responseEntity.getStatusCodeValue(),
            scaStatusResponse,
            responseHeadersMapper.getHeaders(responseEntity.getHeaders())
        );
    }
}