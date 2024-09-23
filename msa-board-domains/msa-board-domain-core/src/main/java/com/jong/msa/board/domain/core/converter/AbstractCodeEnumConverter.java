package com.jong.msa.board.domain.core.converter;

import java.util.EnumSet;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.jong.msa.board.common.enums.CodeEnum;
import com.jong.msa.board.common.enums.CodeEnum.Gender;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.common.enums.CodeEnum.State;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCodeEnumConverter<E extends Enum<E> & CodeEnum<V>, V> implements AttributeConverter<E, V> {
 
	private final Class<E> enumType;
	
	@Override
	public V convertToDatabaseColumn(E attribute) {
		
		return (attribute == null) ? null : attribute.getCode();
	}
	
	@Override
	public E convertToEntityAttribute(V dbData) {

		return (dbData == null) ? null : EnumSet.allOf(this.enumType).stream()
				.filter(e -> e.getCode().equals(dbData)).findAny()
				.orElseThrow(() -> new EnumConstantNotPresentException(this.enumType, (String) dbData));
	}
	
	@Converter(autoApply = true)
	public static class GenderAttributeConverter extends AbstractCodeEnumConverter<Gender, Character> {

		protected GenderAttributeConverter() {
			
			super(Gender.class);
		}
	}

	@Converter(autoApply = true)
	public static class GroupAttributeConverter extends AbstractCodeEnumConverter<Group, Integer> {

		protected GroupAttributeConverter() {
			
			super(Group.class);
		}
	}

	@Converter(autoApply = true)
	public static class StateAttributeConverter extends AbstractCodeEnumConverter<State, Integer> {

		protected StateAttributeConverter() {
			
			super(State.class);
		}
	}

}
