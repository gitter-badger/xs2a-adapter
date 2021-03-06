package de.adorsys.xs2a.adapter.remote.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.xs2a.adapter.api.model.BookingStatus;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import de.adorsys.xs2a.adapter.api.model.PaymentService;
import de.adorsys.xs2a.adapter.api.model.PeriodicPaymentInitiationMultipartBody;
import de.adorsys.xs2a.adapter.config.PeriodicPaymentInitiationMultipartBodyHttpMessageConverter;
import feign.Contract;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class FeignConfiguration {

    @Bean
    public Contract feignContract() {
        return new SpringMvcContract(Collections.emptyList(), new CustomConversionService());
    }

    public static class CustomConversionService extends DefaultConversionService {

        public CustomConversionService() {

            addConverter(new Converter<BookingStatus, String>() {
                @Override
                public String convert(BookingStatus source) {
                    return source.toString();
                }
            });

            addConverter(new Converter<PaymentProduct, String>() {
                @Override
                public String convert(PaymentProduct source) {
                    return source.toString();
                }
            });

            addConverter(new Converter<PaymentService, String>() {
                @Override
                public String convert(PaymentService source) {
                    return source.toString();
                }
            });
        }
    }

    @Bean
    Encoder encoder(ObjectFactory<HttpMessageConverters> messageConverters, ObjectMapper objectMapper) {
        PeriodicPaymentInitiationMultipartBodyHttpMessageConverter httpMessageConverter =
            new PeriodicPaymentInitiationMultipartBodyHttpMessageConverter(objectMapper);

        return new SpringEncoder(new SpringFormEncoder() {
            @Override
            public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
                if (!(object instanceof PeriodicPaymentInitiationMultipartBody)) {
                    super.encode(object, bodyType, template);
                    return;
                }
                BufferingOutputMessage outputMessage = new BufferingOutputMessage();
                try {
                    httpMessageConverter.write((PeriodicPaymentInitiationMultipartBody) object,
                        MediaType.MULTIPART_FORM_DATA,
                        outputMessage);
                } catch (IOException e) {
                    throw new EncodeException(e.getMessage(), e);
                }
                template.body(outputMessage.getOutputStream().toByteArray(), StandardCharsets.UTF_8);
                template.removeHeader(HttpHeaders.CONTENT_TYPE);
                template.header(HttpHeaders.CONTENT_TYPE, outputMessage.getHeaders().getContentType().toString());
            }
        }, messageConverters);
    }

    private static final class BufferingOutputMessage implements HttpOutputMessage {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        private final HttpHeaders httpHeaders = new HttpHeaders();

        @Override
        public OutputStream getBody() throws IOException {
            return this.outputStream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.httpHeaders;
        }

        public ByteArrayOutputStream getOutputStream() {
            return this.outputStream;
        }

    }
}
