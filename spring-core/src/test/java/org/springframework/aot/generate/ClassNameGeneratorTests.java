/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aot.generate;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import org.springframework.javapoet.ClassName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link ClassNameGenerator}.
 *
 * @author Phillip Webb
 */
class ClassNameGeneratorTests {

	private final ClassNameGenerator generator = new ClassNameGenerator();

	@Test
	void generateClassNameWhenTargetClassIsNullUsesAotPackage() {
		ClassName generated = this.generator.generateClassName((Class<?>) null, "test");
		assertThat(generated).hasToString("__.Test");
	}

	@Test
	void generatedClassNameWhenFeatureIsEmptyThrowsException() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> this.generator.generateClassName(InputStream.class, ""))
				.withMessage("'featureName' must not be empty");
	}

	@Test
	void generatedClassNameWhenFeatureIsNotAllLettersThrowsException() {
		assertThat(this.generator.generateClassName(InputStream.class, "name!"))
				.hasToString("java.io.InputStream__Name");
		assertThat(this.generator.generateClassName(InputStream.class, "1NameHere"))
				.hasToString("java.io.InputStream__NameHere");
		assertThat(this.generator.generateClassName(InputStream.class, "Y0pe"))
				.hasToString("java.io.InputStream__YPe");
	}

	@Test
	void generateClassNameWithClassWhenLowercaseFeatureNameGeneratesName() {
		ClassName generated = this.generator.generateClassName(InputStream.class,
				"bytes");
		assertThat(generated).hasToString("java.io.InputStream__Bytes");
	}

	@Test
	void generateClassNameWithClassWhenInnerClassGeneratesName() {
		ClassName generated = this.generator.generateClassName(TestBean.class, "EventListener");
		assertThat(generated)
			.hasToString("org.springframework.aot.generate.ClassNameGeneratorTests_TestBean__EventListener");
	}

	@Test
	void generateClassWithClassWhenMultipleCallsGeneratesSequencedName() {
		ClassName generated1 = this.generator.generateClassName(InputStream.class, "bytes");
		ClassName generated2 = this.generator.generateClassName(InputStream.class, "bytes");
		ClassName generated3 = this.generator.generateClassName(InputStream.class, "bytes");
		assertThat(generated1).hasToString("java.io.InputStream__Bytes");
		assertThat(generated2).hasToString("java.io.InputStream__Bytes1");
		assertThat(generated3).hasToString("java.io.InputStream__Bytes2");
	}

	static class TestBean {

	}

}
