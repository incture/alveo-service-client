package com.ap.menabev.invoice;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceHeaderDo;

@Repository
public class InvoiceHeaderRepoFilter implements InvoiceHeaderFilterRepo{
	
	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceHeaderDo> getFilterDetails(String query) {
		// TODO Auto-generated method stub
		return entityManager.createNativeQuery(query.toString(), InvoiceHeaderDo.class).getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<InvoiceHeaderDo> getFilteredRequestIds(String query) {
		// TODO Auto-generated method stub
		return entityManager.createNativeQuery(query.toString(), InvoiceHeaderDo.class).getResultList();
	}
	

}
