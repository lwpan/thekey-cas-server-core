package org.ccci.gto.cas.oauth.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.net.URI;
import java.util.Set;

@Entity
@Table(name = "OAuthClients")
public class Client implements Serializable {
    private static final long serialVersionUID = -539527167978624111L;

    private static final URI MOBILE_URI = URI.create("thekey:/oauth/mobile").normalize();

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private String description;

    private String contactEmail;

    private boolean mobile;

    private String redirectUri;

    private String templateCssUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client", cascade = { CascadeType.REMOVE })
    private Set<Code> codes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client", cascade = { CascadeType.REMOVE })
    private Set<Token> tokens;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public void setContactEmail(final String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isMobile() {
        return this.mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public String getTemplateCssUrl() {
        return this.templateCssUrl;
    }

    public void setTemplateCssUrl(final String templateCssUrl) {
        this.templateCssUrl = templateCssUrl;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public void setRedirectUri(final String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public boolean isValidRedirectUri(final URI uri) {
        if (this.isMobile()) {
            return checkUri(uri.normalize(), MOBILE_URI);
        }
        return false;
    }

    private static boolean checkUri(final URI uri, final URI pattern) {
        // make sure we have a uri and a pattern
        if (uri == null || pattern == null) {
            return false;
        }
        // ensure there is a valid scheme
        if (uri.getScheme() == null || !uri.getScheme().equals(pattern.getScheme())) {
            return false;
        }

        // handle opaque URIs, the scheme specific part needs to match
        if (uri.isOpaque() || pattern.isOpaque()) {
            return uri.getRawSchemeSpecificPart().equals(pattern.getRawSchemeSpecificPart());
        }

        // treat pattern as a root uri and make sure the specified uri is a
        // descendant uri
        return (uri.getRawAuthority() == null ? pattern.getRawAuthority() == null : uri.getRawAuthority().equals(
                pattern.getRawAuthority()))
                && (uri.getRawPath() == null ? pattern.getRawPath() == null : uri.getRawPath().startsWith(
                        pattern.getRawPath()));
    }
}
