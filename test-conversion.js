// Test conversion functions
function convertSeverityForBackend(newSeverity) {
    const severityConversion = {
        'CRITICAL': 'SEV1',
        'HIGH': 'SEV2',
        'MEDIUM': 'SEV3',
        'LOW': 'SEV4'
    };
    return severityConversion[newSeverity] || newSeverity;
}

// Test
console.log('CRITICAL ->', convertSeverityForBackend('CRITICAL'));
console.log('HIGH ->', convertSeverityForBackend('HIGH'));
console.log('MEDIUM ->', convertSeverityForBackend('MEDIUM'));
console.log('LOW ->', convertSeverityForBackend('LOW'));
console.log('UNKNOWN ->', convertSeverityForBackend('UNKNOWN'));
