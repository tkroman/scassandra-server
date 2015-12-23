package org.scassandra.http.client;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class MultiPrimeRequestTest {

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(MultiPrimeRequest.class).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(MultiPrimeRequest.Action.class).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(MultiPrimeRequest.Match.class).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(MultiPrimeRequest.When.class).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(MultiPrimeRequest.Then.class).allFieldsShouldBeUsed().verify();
        EqualsVerifier.forClass(MultiPrimeRequest.VariableMatch.class).allFieldsShouldBeUsed().verify();
    }
}