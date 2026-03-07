# Risk Matrix Creation - Cybersecurity Assessment

## Asset Identification

### 1. MySQL Database
**Description:** Core database system storing critical application data
**Value:** High - Contains all persistent data for the application

### 2. Faculty Personal Data & Academic Records
**Description:** Sensitive personal information and academic records of faculty members
**Value:** Critical - Protected by data privacy regulations (GDPR, FERPA)

### 3. JWT Secret Keys & Environment Variables
**Description:** Authentication tokens and configuration secrets
**Value:** Critical - Compromise leads to complete system access

### 4. Amazon Web Services (AWS) Infrastructure
**Description:** Cloud infrastructure hosting the application
**Value:** High - Foundation for all services and operations

### 5. Uploaded Accreditation Evidence
**Description:** Documents and files uploaded for institutional accreditation
**Value:** High - Essential for institutional compliance and reputation

---

## Risk Assessment Matrix

| Asset | Threat | Vulnerability | Likelihood (1-5) | Impact (1-5) | Risk Score | Treatment |
|-------|--------|---------------|------------------|--------------|------------|-----------|
| **MySQL Database** | Unauthorized Access | Weak or default passwords, exposed database ports, lack of multi-factor authentication | 4 | 5 | 20 | **Reduce**: Enforce strong password policies, enable MFA, restrict database access using firewalls and private networks, disable default accounts |
| **Faculty Personal Data & Academic Records** | Data Breach / Data Leakage & Legal Liability | No encryption methods, poor role-based access control (RBAC), improper data access permissions | 4 | 5 | 20 | **Transfer**: Purchase cyber liability insurance to cover legal costs and penalties. **Reduce**: Implement end-to-end encryption, enforce strict RBAC, conduct regular access audits |
| **JWT Secret Keys & Environment Variables** | Privilege Escalation | Hardcoded secrets in code, weak key generation, exposed .env files, insufficient key rotation | 3 | 5 | 15 | **Reduce**: Use secure secret management services (AWS Secrets Manager, HashiCorp Vault), implement automatic key rotation, never commit secrets to version control |
| **Amazon Web Services (AWS) Infrastructure** | Resource Hijacking | Misconfigured security groups, overly permissive IAM policies, unpatched services, lack of monitoring | 3 | 4 | 12 | **Transfer**: Leverage AWS shared responsibility model and AWS Shield for DDoS protection. **Reduce**: Implement least privilege IAM policies, enable CloudTrail and GuardDuty |
| **Uploaded Accreditation Evidence** | Ransomware | Lack of backup strategy, no file integrity monitoring, insufficient access controls, missing antivirus scanning | 3 | 4 | 12 | **Accept**: Small risk of data loss after implementing backups. **Reduce**: Implement automated backups with versioning, enable file integrity monitoring, scan uploads for malware |

---

## Detailed Risk Analysis

### Risk Score Calculation
**Risk Score = Likelihood × Impact**

- **1-5**: Low Risk (Green)
- **6-10**: Medium Risk (Yellow)
- **11-15**: High Risk (Orange)
- **16-25**: Critical Risk (Red)

---

## Asset 1: MySQL Database

### Threat: Unauthorized Access
**Vulnerabilities:**
- Weak or default passwords
- Exposed database ports
- Lack of multi-factor authentication

**Likelihood: 4** (Likely)
- Database ports are commonly scanned by attackers
- Default credentials are widely known
- Many systems lack MFA implementation

**Impact: 5** (Catastrophic)
- Complete data compromise
- System-wide breach
- Regulatory penalties

**Risk Score: 20** (Critical)

**Treatment: REDUCE**
- Enforce strong password policies (minimum 12 characters, complexity requirements)
- Enable multi-factor authentication for database access
- Restrict database access using firewalls and private networks
- Disable default accounts (root, admin)
- Implement IP whitelisting
- Use VPN for remote database access

---

## Asset 2: Faculty Personal Data & Academic Records

### Threat: Data Breach / Data Leakage & Legal Liability
**Vulnerabilities:**
- No encryption methods
- Poor role-based access control (RBAC)
- Improper data access permissions

**Likelihood: 4** (Likely)
- High-value target for attackers
- Common misconfiguration issues
- Insider threat potential

**Impact: 5** (Catastrophic)
- Legal liability and regulatory fines
- Reputation damage
- Loss of trust
- GDPR/FERPA violations

**Risk Score: 20** (Critical)

**Treatment: TRANSFER + REDUCE**

**Transfer:**
- Purchase cyber liability insurance to cover potential legal costs, regulatory fines, and breach notification expenses
- Engage third-party data protection officer (DPO) services for compliance management
- Use managed security service providers (MSSP) for 24/7 monitoring

**Reduce:**
- Implement end-to-end encryption (AES-256 at rest, TLS 1.3 in transit)
- Enforce strict role-based access control (RBAC)
- Conduct regular access audits and reviews
- Implement data loss prevention (DLP) tools
- Enable database activity monitoring
- Implement data classification and handling policies
- Regular compliance audits

---

## Asset 3: JWT Secret Keys & Environment Variables

### Threat: Privilege Escalation
**Vulnerabilities:**
- Hardcoded secrets in code
- Weak key generation
- Exposed .env files
- Insufficient key rotation

**Likelihood: 3** (Possible)
- Secrets may be exposed in version control
- Configuration files may be publicly accessible
- Developers may not follow best practices

**Impact: 5** (Catastrophic)
- Complete authentication bypass
- Unauthorized access to all user accounts
- System-wide compromise

**Risk Score: 15** (High)

**Treatment: REDUCE**
- Use secure secret management services (AWS Secrets Manager, HashiCorp Vault)
- Implement automatic key rotation (every 90 days minimum)
- Never commit secrets to version control
- Use .gitignore for environment files
- Implement secret scanning in CI/CD pipeline
- Use strong cryptographic key generation (256-bit minimum)
- Separate secrets per environment (dev, staging, production)

---

## Asset 4: Amazon Web Services (AWS) Infrastructure

### Threat: Resource Hijacking
**Vulnerabilities:**
- Misconfigured security groups
- Overly permissive IAM policies
- Unpatched services
- Lack of monitoring

**Likelihood: 3** (Possible)
- Cloud misconfigurations are common
- Complex IAM policies lead to errors
- Automated scanning by attackers

**Impact: 4** (Major)
- Unauthorized resource usage
- Financial loss from crypto-mining
- Service disruption
- Data exfiltration

**Risk Score: 12** (High)

**Treatment: TRANSFER + REDUCE**

**Transfer:**
- Leverage AWS shared responsibility model (AWS manages infrastructure security)
- Use AWS Shield Standard (free DDoS protection)
- Consider AWS Shield Advanced for enterprise-level DDoS protection
- AWS handles physical security and hardware maintenance

**Reduce:**
- Implement least privilege IAM policies
- Enable AWS CloudTrail for audit logging
- Enable AWS GuardDuty for threat detection
- Use AWS Config for compliance monitoring
- Regular security audits and penetration testing
- Implement billing alerts and anomaly detection
- Use AWS Security Hub for centralized security management
- Enable MFA for all AWS accounts
- Restrict security group rules to specific IP ranges

---

## Asset 5: Uploaded Accreditation Evidence

### Threat: Ransomware
**Vulnerabilities:**
- Lack of backup strategy
- No file integrity monitoring
- Insufficient access controls
- Missing antivirus scanning

**Likelihood: 3** (Possible)
- Ransomware attacks are increasingly common
- File uploads are common attack vectors
- Educational institutions are frequent targets

**Impact: 4** (Major)
- Loss of critical accreditation documents
- Operational disruption
- Reputation damage
- Potential accreditation delays

**Risk Score: 12** (High)

**Treatment: ACCEPT + REDUCE**

**Accept:**
- Accept residual risk of minor data loss (less than 24 hours) after implementing comprehensive backup strategy
- Accept low probability of successful ransomware attack after implementing all controls
- Document accepted risk level and obtain management approval

**Reduce:**
- Implement automated backups with versioning (3-2-1 backup strategy)
- Enable file integrity monitoring
- Scan all uploads for malware using antivirus/anti-malware
- Maintain offline backup copies (air-gapped)
- Implement access controls and audit logging
- Regular backup restoration testing
- User training on phishing and social engineering
- Implement email filtering and web filtering
- Keep systems patched and updated

---

## Risk Treatment Strategies Summary

### AVOID
Not used for these assets as they are all essential to business operations. Avoiding these risks would mean discontinuing critical services.

### REDUCE (Primary Strategy - Used in all 5 assets)
- **MySQL Database**: Implement technical controls (MFA, firewalls, strong passwords)
- **Faculty Data**: Encryption, RBAC, access audits
- **JWT Secrets**: Secret management services, key rotation
- **AWS Infrastructure**: Least privilege policies, monitoring tools
- **Accreditation Files**: Backups, malware scanning, access controls

### TRANSFER (Used in 2 assets)
- **Faculty Data**: Cyber liability insurance for legal costs and regulatory fines
- **AWS Infrastructure**: Leverage AWS shared responsibility model and AWS Shield for DDoS protection

### ACCEPT (Used in 1 asset)
- **Accreditation Files**: Accept residual risk of minor data loss (less than 24 hours) after implementing comprehensive backup strategy
- Accept low probability of successful attack after all controls are in place

---

## Implementation Priority

### Immediate (Critical - Risk Score 16-25)
1. MySQL Database - Unauthorized Access (Score: 20)
2. Faculty Personal Data - Data Breach (Score: 20)

### High Priority (High - Risk Score 11-15)
3. JWT Secret Keys - Privilege Escalation (Score: 15)
4. AWS Infrastructure - Resource Hijacking (Score: 12)
5. Accreditation Evidence - Ransomware (Score: 12)

---

## Monitoring and Review

- **Quarterly risk assessments** to update likelihood and impact scores
- **Monthly security audits** of implemented controls
- **Continuous monitoring** using SIEM and security tools
- **Annual penetration testing** and vulnerability assessments
- **Incident response plan** review and testing

---

## Compliance Considerations

- **GDPR**: Faculty personal data protection
- **FERPA**: Academic records privacy
- **ISO 27001**: Information security management
- **NIST Cybersecurity Framework**: Risk management approach
- **SOC 2**: Cloud infrastructure security controls

---

**Document Version:** 1.0  
**Last Updated:** March 7, 2026  
**Next Review Date:** June 7, 2026
