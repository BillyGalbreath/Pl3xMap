/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.util;

import io.undertow.attribute.AuthenticationTypeExchangeAttribute;
import io.undertow.attribute.BytesSentAttribute;
import io.undertow.attribute.CookieAttribute;
import io.undertow.attribute.DateTimeAttribute;
import io.undertow.attribute.HostAndPortAttribute;
import io.undertow.attribute.IdentUsernameAttribute;
import io.undertow.attribute.LocalIPAttribute;
import io.undertow.attribute.LocalPortAttribute;
import io.undertow.attribute.LocalServerNameAttribute;
import io.undertow.attribute.NullAttribute;
import io.undertow.attribute.PathParameterAttribute;
import io.undertow.attribute.PredicateContextAttribute;
import io.undertow.attribute.QueryParameterAttribute;
import io.undertow.attribute.QueryStringAttribute;
import io.undertow.attribute.RelativePathAttribute;
import io.undertow.attribute.RemoteHostAttribute;
import io.undertow.attribute.RemoteIPAttribute;
import io.undertow.attribute.RemoteObfuscatedIPAttribute;
import io.undertow.attribute.RemoteUserAttribute;
import io.undertow.attribute.RequestCookieAttribute;
import io.undertow.attribute.RequestHeaderAttribute;
import io.undertow.attribute.RequestLineAttribute;
import io.undertow.attribute.RequestMethodAttribute;
import io.undertow.attribute.RequestPathAttribute;
import io.undertow.attribute.RequestProtocolAttribute;
import io.undertow.attribute.RequestSchemeAttribute;
import io.undertow.attribute.RequestURLAttribute;
import io.undertow.attribute.ResolvedPathAttribute;
import io.undertow.attribute.ResponseCodeAttribute;
import io.undertow.attribute.ResponseCookieAttribute;
import io.undertow.attribute.ResponseHeaderAttribute;
import io.undertow.attribute.ResponseReasonPhraseAttribute;
import io.undertow.attribute.ResponseTimeAttribute;
import io.undertow.attribute.SecureExchangeAttribute;
import io.undertow.attribute.SslCipherAttribute;
import io.undertow.attribute.SslClientCertAttribute;
import io.undertow.attribute.SslSessionIdAttribute;
import io.undertow.attribute.StoredResponse;
import io.undertow.attribute.ThreadNameAttribute;
import io.undertow.attribute.TransportProtocolAttribute;
import io.undertow.client.ajp.AjpClientProvider;
import io.undertow.client.http.HttpClientProvider;
import io.undertow.client.http2.Http2ClearClientProvider;
import io.undertow.client.http2.Http2ClientProvider;
import io.undertow.client.http2.Http2PriorKnowledgeClientProvider;
import io.undertow.predicate.AuthenticationRequiredPredicate;
import io.undertow.predicate.ContainsPredicate;
import io.undertow.predicate.EqualsPredicate;
import io.undertow.predicate.ExistsPredicate;
import io.undertow.predicate.IdempotentPredicate;
import io.undertow.predicate.MaxContentSizePredicate;
import io.undertow.predicate.MethodPredicate;
import io.undertow.predicate.MinContentSizePredicate;
import io.undertow.predicate.PathMatchPredicate;
import io.undertow.predicate.PathPrefixPredicate;
import io.undertow.predicate.PathSuffixPredicate;
import io.undertow.predicate.PathTemplatePredicate;
import io.undertow.predicate.PredicatesHandler;
import io.undertow.predicate.RegularExpressionPredicate;
import io.undertow.predicate.RequestLargerThanPredicate;
import io.undertow.predicate.RequestSmallerThanPredicate;
import io.undertow.predicate.SecurePredicate;
import io.undertow.protocols.alpn.DefaultAlpnEngineManager;
import io.undertow.protocols.alpn.JettyAlpnProvider;
import io.undertow.protocols.alpn.ModularJdkAlpnProvider;
import io.undertow.protocols.alpn.OpenSSLAlpnProvider;
import io.undertow.protocols.ssl.SNIAlpnEngineManager;
import io.undertow.server.JvmRouteHandler;
import io.undertow.server.handlers.AccessControlListHandler;
import io.undertow.server.handlers.ActiveRequestTrackerHandler;
import io.undertow.server.handlers.AllowedMethodsHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.ByteRangeHandler;
import io.undertow.server.handlers.CanonicalPathHandler;
import io.undertow.server.handlers.DisableCacheHandler;
import io.undertow.server.handlers.DisallowedMethodsHandler;
import io.undertow.server.handlers.ForwardedHandler;
import io.undertow.server.handlers.HttpContinueAcceptingHandler;
import io.undertow.server.handlers.HttpTraceHandler;
import io.undertow.server.handlers.IPAddressAccessControlHandler;
import io.undertow.server.handlers.JDBCLogHandler;
import io.undertow.server.handlers.LearningPushHandler;
import io.undertow.server.handlers.LocalNameResolvingHandler;
import io.undertow.server.handlers.PathSeparatorHandler;
import io.undertow.server.handlers.PeerNameResolvingHandler;
import io.undertow.server.handlers.ProxyPeerAddressHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.RequestBufferingHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.server.handlers.ResponseRateLimitingHandler;
import io.undertow.server.handlers.SSLHeaderHandler;
import io.undertow.server.handlers.SameSiteCookieHandler;
import io.undertow.server.handlers.SecureCookieHandler;
import io.undertow.server.handlers.SetAttributeHandler;
import io.undertow.server.handlers.SetErrorHandler;
import io.undertow.server.handlers.SetHeaderHandler;
import io.undertow.server.handlers.StoredResponseHandler;
import io.undertow.server.handlers.StuckThreadDetectionHandler;
import io.undertow.server.handlers.URLDecodingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.builder.ResponseCodeHandlerBuilder;
import io.undertow.server.handlers.builder.RewriteHandlerBuilder;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.RequestEncodingHandler;
import io.undertow.server.handlers.error.FileErrorPageHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.proxy.ProxyHandlerBuilder;
import io.undertow.server.handlers.resource.ResourceHandler;
import javax.imageio.spi.IIORegistry;
import org.xnio.nio.NioXnioProvider;

@SuppressWarnings("deprecation")
public class SpiFix {
    public static void forceRegisterSpis() {
        IIORegistry registry = IIORegistry.getDefaultInstance();

        // io.undertow.attribute.ExchangeAttributeBuilder
        registry.registerServiceProvider(new RelativePathAttribute.Builder());
        registry.registerServiceProvider(new RemoteIPAttribute.Builder());
        registry.registerServiceProvider(new LocalIPAttribute.Builder());
        registry.registerServiceProvider(new RequestProtocolAttribute.Builder());
        registry.registerServiceProvider(new LocalPortAttribute.Builder());
        registry.registerServiceProvider(new IdentUsernameAttribute.Builder());
        registry.registerServiceProvider(new RequestMethodAttribute.Builder());
        registry.registerServiceProvider(new QueryStringAttribute.Builder());
        registry.registerServiceProvider(new RequestLineAttribute.Builder());
        registry.registerServiceProvider(new BytesSentAttribute.Builder());
        registry.registerServiceProvider(new DateTimeAttribute.Builder());
        registry.registerServiceProvider(new RemoteUserAttribute.Builder());
        registry.registerServiceProvider(new RequestURLAttribute.Builder());
        registry.registerServiceProvider(new ThreadNameAttribute.Builder());
        registry.registerServiceProvider(new LocalServerNameAttribute.Builder());
        registry.registerServiceProvider(new RequestHeaderAttribute.Builder());
        registry.registerServiceProvider(new ResponseHeaderAttribute.Builder());
        registry.registerServiceProvider(new CookieAttribute.Builder());
        registry.registerServiceProvider(new RequestCookieAttribute.Builder());
        registry.registerServiceProvider(new ResponseCookieAttribute.Builder());
        registry.registerServiceProvider(new ResponseCodeAttribute.Builder());
        registry.registerServiceProvider(new PredicateContextAttribute.Builder());
        registry.registerServiceProvider(new QueryParameterAttribute.Builder());
        registry.registerServiceProvider(new SslClientCertAttribute.Builder());
        registry.registerServiceProvider(new SslCipherAttribute.Builder());
        registry.registerServiceProvider(new SslSessionIdAttribute.Builder());
        registry.registerServiceProvider(new ResponseTimeAttribute.Builder());
        registry.registerServiceProvider(new PathParameterAttribute.Builder());
        registry.registerServiceProvider(new TransportProtocolAttribute.Builder());
        registry.registerServiceProvider(new RequestSchemeAttribute.Builder());
        registry.registerServiceProvider(new HostAndPortAttribute.Builder());
        registry.registerServiceProvider(new AuthenticationTypeExchangeAttribute.Builder());
        registry.registerServiceProvider(new SecureExchangeAttribute.Builder());
        registry.registerServiceProvider(new RemoteHostAttribute.Builder());
        registry.registerServiceProvider(new RequestPathAttribute.Builder());
        registry.registerServiceProvider(new ResolvedPathAttribute.Builder());
        registry.registerServiceProvider(new NullAttribute.Builder());
        registry.registerServiceProvider(new StoredResponse.Builder());
        registry.registerServiceProvider(new ResponseReasonPhraseAttribute.Builder());
        registry.registerServiceProvider(new RemoteObfuscatedIPAttribute.Builder());

        // io.undertow.client.ClientProvider
        registry.registerServiceProvider(new HttpClientProvider());
        registry.registerServiceProvider(new AjpClientProvider());
        registry.registerServiceProvider(new Http2ClientProvider());
        registry.registerServiceProvider(new Http2ClearClientProvider());
        registry.registerServiceProvider(new Http2PriorKnowledgeClientProvider());

        // io.undertow.predicate.PredicateBuilder
        registry.registerServiceProvider(new PathMatchPredicate.Builder());
        registry.registerServiceProvider(new PathPrefixPredicate.Builder());
        registry.registerServiceProvider(new ContainsPredicate.Builder());
        registry.registerServiceProvider(new ExistsPredicate.Builder());
        registry.registerServiceProvider(new RegularExpressionPredicate.Builder());
        registry.registerServiceProvider(new PathSuffixPredicate.Builder());
        registry.registerServiceProvider(new EqualsPredicate.Builder());
        registry.registerServiceProvider(new PathTemplatePredicate.Builder());
        registry.registerServiceProvider(new MethodPredicate.Builder());
        registry.registerServiceProvider(new AuthenticationRequiredPredicate.Builder());
        registry.registerServiceProvider(new MaxContentSizePredicate.Builder());
        registry.registerServiceProvider(new MinContentSizePredicate.Builder());
        registry.registerServiceProvider(new SecurePredicate.Builder());
        registry.registerServiceProvider(new IdempotentPredicate.Builder());
        registry.registerServiceProvider(new RequestLargerThanPredicate.Builder());
        registry.registerServiceProvider(new RequestSmallerThanPredicate.Builder());

        // io.undertow.protocols.alpn.ALPNEngineManager
        registry.registerServiceProvider(new SNIAlpnEngineManager());
        registry.registerServiceProvider(new DefaultAlpnEngineManager());

        // io.undertow.protocols.alpn.ALPNProvider
        registry.registerServiceProvider(new JettyAlpnProvider());
        registry.registerServiceProvider(new ModularJdkAlpnProvider());
        registry.registerServiceProvider(new OpenSSLAlpnProvider());

        // io.undertow.server.handlers.builder.HandlerBuilder
        registry.registerServiceProvider(new RewriteHandlerBuilder());
        registry.registerServiceProvider(new SetAttributeHandler.Builder());
        registry.registerServiceProvider(new SetAttributeHandler.ClearBuilder());
        registry.registerServiceProvider(new ResponseCodeHandlerBuilder());
        registry.registerServiceProvider(new DisableCacheHandler.Builder());
        registry.registerServiceProvider(new ProxyPeerAddressHandler.Builder());
        registry.registerServiceProvider(new ProxyHandlerBuilder());
        registry.registerServiceProvider(new RedirectHandler.Builder());
        registry.registerServiceProvider(new AccessLogHandler.Builder());
        registry.registerServiceProvider(new ActiveRequestTrackerHandler.Builder());
        registry.registerServiceProvider(new AllowedMethodsHandler.Builder());
        registry.registerServiceProvider(new BlockingHandler.Builder());
        registry.registerServiceProvider(new CanonicalPathHandler.Builder());
        registry.registerServiceProvider(new DisallowedMethodsHandler.Builder());
        registry.registerServiceProvider(new FileErrorPageHandler.Builder());
        registry.registerServiceProvider(new HttpTraceHandler.Builder());
        registry.registerServiceProvider(new JvmRouteHandler.Builder());
        registry.registerServiceProvider(new PeerNameResolvingHandler.Builder());
        registry.registerServiceProvider(new RequestDumpingHandler.Builder());
        registry.registerServiceProvider(new RequestLimitingHandler.Builder());
        registry.registerServiceProvider(new ResourceHandler.Builder());
        registry.registerServiceProvider(new SSLHeaderHandler.Builder());
        registry.registerServiceProvider(new ResponseRateLimitingHandler.Builder());
        registry.registerServiceProvider(new URLDecodingHandler.Builder());
        registry.registerServiceProvider(new PathSeparatorHandler.Builder());
        registry.registerServiceProvider(new IPAddressAccessControlHandler.Builder());
        registry.registerServiceProvider(new ByteRangeHandler.Builder());
        registry.registerServiceProvider(new EncodingHandler.Builder());
        registry.registerServiceProvider(new RequestEncodingHandler.Builder());
        registry.registerServiceProvider(new LearningPushHandler.Builder());
        registry.registerServiceProvider(new SetHeaderHandler.Builder());
        registry.registerServiceProvider(new PredicatesHandler.DoneHandlerBuilder());
        registry.registerServiceProvider(new PredicatesHandler.RestartHandlerBuilder());
        registry.registerServiceProvider(new RequestBufferingHandler.Builder());
        registry.registerServiceProvider(new StuckThreadDetectionHandler.Builder());
        registry.registerServiceProvider(new AccessControlListHandler.Builder());
        registry.registerServiceProvider(new JDBCLogHandler.Builder());
        registry.registerServiceProvider(new LocalNameResolvingHandler.Builder());
        registry.registerServiceProvider(new StoredResponseHandler.Builder());
        registry.registerServiceProvider(new SecureCookieHandler.Builder());
        registry.registerServiceProvider(new ForwardedHandler.Builder());
        registry.registerServiceProvider(new HttpContinueAcceptingHandler.Builder());
        registry.registerServiceProvider(new EagerFormParsingHandler.Builder());
        registry.registerServiceProvider(new SameSiteCookieHandler.Builder());
        registry.registerServiceProvider(new SetErrorHandler.Builder());

        // org.xnio.XnioProvider
        registry.registerServiceProvider(new NioXnioProvider());
    }
}
