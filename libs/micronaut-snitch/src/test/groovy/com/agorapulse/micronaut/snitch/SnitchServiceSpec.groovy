/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.micronaut.snitch

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import io.micronaut.context.ApplicationContext
import io.micronaut.inject.qualifiers.Qualifiers
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.inject.Singleton

@CompileDynamic
class SnitchServiceSpec extends Specification {

    SnitchClient client = Mock(SnitchClient)

    @AutoCleanup ApplicationContext context = null

    void 'snitch client uses the default value'() {
        when:
            context = ApplicationContext.run()
        then:
            context.getBean(SnitchClient)

        when:
            // id from the docs
            // https://deadmanssnitch.com/docs
            boolean result = context.getBean(SnitchClient).snitch('cde9e48a68', '1')
        then:
            result
    }

    void 'no config'() {
        given:
            context = ApplicationContext.build().start()
        expect:
            context.getBeanDefinitions(SnitchService).size() == 1
            context.getBean(SnitchService) instanceof NoopSnitchService
            context.getBean(SnitchService).snitch()
    }

    void 'no config interception works'() {
        when:
            context = ApplicationContext.run()
            SnitchTester tester = context.getBean(SnitchTester)
        and:
            tester.success()
        then:
            noExceptionThrown()

        when:
            tester.failure()
        then:
            thrown(IllegalStateException)
    }

    void 'just url'() {
        given:
            context = ApplicationContext.build().properties('micronaut.http.services.snitch.urls': 'https://localhost:12345').start()
        expect:
            context.getBeanDefinitions(SnitchJobConfiguration).size() == 0
            context.getBeanDefinitions(SnitchService).size() == 1
            context.getBean(SnitchService) instanceof NoopSnitchService
    }

    void 'test default config'() {
        given:
            context = ApplicationContext
                    .build()
                    .properties(
                        'snitches.id': 'cafebabe'
                    )
                    .build()
                    .registerSingleton(SnitchClient, client)
                    .start()

        expect:
            context.getBeansOfType(SnitchService).size() == 1
            context.getBeanDefinitions(SnitchService).size() == 1
        when:
            assert context.getBean(SnitchService).snitch()
        then:
            1 * client.snitch('cafebabe', '0') >> DefaultSnitchService.OK
    }

    void 'test default config interceptor success'() {
        given:
            context = ApplicationContext
                    .build()
                    .properties(
                            'snitches.id': 'cafebabe'
                    )
                    .build()
                    .registerSingleton(SnitchClient, client)
                    .start()

        when:
            SnitchTester tester = context.getBean(SnitchTester)

            tester.success()
        then:
            1 * client.snitch('cafebabe', '0') >> DefaultSnitchService.OK
    }

    void 'test default config interceptor failure'() {
        given:
            context = ApplicationContext
                    .build()
                    .properties(
                            'snitches.id': 'cafebabe'
                    )
                    .build()
                    .registerSingleton(SnitchClient, client)
                    .start()

        when:
            SnitchTester tester = context.getBean(SnitchTester)

            tester.failure()
        then:
            thrown(IllegalStateException)

            1 * client.snitch('cafebabe', '1') >> DefaultSnitchService.OK
    }

    void 'test rate limit success'() {
        given:
        context = ApplicationContext
                .build()
                .properties(
                        'snitches.id': 'cafebabe'
                )
                .build()
                .registerSingleton(SnitchClient, client)
                .start()

        when:
        SnitchTester tester = context.getBean(SnitchTester)

        tester.success()
        tester.success() // Should not call
        then:
        1 * client.snitch('cafebabe', '0') >> DefaultSnitchService.OK
    }

    void 'test multiple config'() {
        given:
            context = ApplicationContext
                    .build()
                    .properties(
                        'snitches.jobs.cafebabe.id': 'cafebabe',
                        'snitches.jobs.babecafe.id': 'babecafe'
                    )
                    .build()
                    .registerSingleton(SnitchClient, client)
                    .start()

        when:
            assert context.getBean(SnitchService, Qualifiers.byName('cafebabe')).snitch()
        then:
            1 * client.snitch('cafebabe', '0') >> DefaultSnitchService.OK

        when:
            assert context.getBean(SnitchService, Qualifiers.byName('babecafe')).snitch(false)
        then:
            1 * client.snitch('babecafe', '1') >> DefaultSnitchService.OK
    }

    void 'test multiple config annotation driven'() {
        given:
            context = ApplicationContext
                    .build()
                    .properties(
                            'snitches.jobs.cafebabe.id': 'cafebabe',
                            'snitches.jobs.babecafe.id': 'babecafe'
                    )
                    .build()
                    .registerSingleton(SnitchClient, client)
                    .start()

        when:
            SnitchTester tester = context.getBean(SnitchTester)
            tester.successBabecafe()
        then:
            1 * client.snitch('babecafe', '0') >> DefaultSnitchService.OK
    }

    void 'test multiple and default config'() {
        given:
            context = ApplicationContext
                    .build()
                    .properties(
                        'snitches.id': 'foobar',
                        'snitches.jobs.cafebabe.id': 'cafebabe',
                        'snitches.jobs.babecafe.id': 'babecafe'
                    )
                    .build()
                    .registerSingleton(SnitchClient, client)
                    .start()

        when:
            assert context.getBean(SnitchService, Qualifiers.byName('default')).snitch()
        then:
            1 * client.snitch('foobar', '0') >> DefaultSnitchService.OK

        when:
            assert context.getBean(SnitchService, Qualifiers.byName('cafebabe')).snitch()
        then:
            1 * client.snitch('cafebabe', '0') >> DefaultSnitchService.OK

        when:
            assert context.getBean(SnitchService, Qualifiers.byName('cafebabe')).snitch()
        then:
            // call within grace period - no call to the backend
            0 * client.snitch('cafebabe', '0') >> DefaultSnitchService.OK

        when:
            assert !context.getBean(SnitchService, Qualifiers.byName('babecafe')).snitch(false)
        then:
            1 * client.snitch('babecafe', '1') >> { throw new IllegalStateException('failed') }
    }

}

@Singleton
@CompileStatic
class SnitchTester {

    @Snitch String success() {
        return 'default'
    }

    @Snitch String failure() {
        throw new IllegalStateException('Ooops!')
    }

    @Snitch(value = 'babecafe', before = true) String successBabecafe() {
        return 'babecafe'
    }

}
