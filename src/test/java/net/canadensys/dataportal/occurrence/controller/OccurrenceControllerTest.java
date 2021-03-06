package net.canadensys.dataportal.occurrence.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.canadensys.dataportal.occurrence.TestDataHelper;
import net.canadensys.dataportal.occurrence.config.OccurrencePortalConfig;
import net.canadensys.dataportal.occurrence.model.OccurrenceModel;
import net.canadensys.dataportal.occurrence.model.OccurrenceViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Testing the Occurrence controller routing and make sure URLs are working.
 * TODO move to new testing framework
 * @WebAppConfiguration()
 * 
 *  @Autowired
 *  private WebApplicationContext wac;
 *  @Before
 *   public void setup() {
 *   mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
 *   .build();
 *    }
 *     @Test
 *      public void test404HttpErrorPage() throws Exception {
 *        mockMvc.perform(get("/not_found"))
 *                .andExpect(status().isNotFound())
 *                          .andExpect(content().string("handleNotFound"));
 *      }
 * @author canadensys
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-dispatcher-servlet.xml"})
public class OccurrenceControllerTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	@Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    
    @Autowired
    private OccurrenceController occurrenceController;
     
    private JdbcTemplate jdbcTemplate;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
    @Before
    public void setup() {
    	TestDataHelper.loadTestData(applicationContext, jdbcTemplate);
    }

    @Test
    public void testIptResourceURL() throws Exception {
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.setMethod("GET");
    	request.setRequestURI("/resources/acad-specimens");
    	//test default view
    	Object handler = handlerMapping.getHandler(request).getHandler();    
    	ModelAndView mav = handlerAdapter.handle(request, response, handler);
    	assertTrue(((RedirectView)mav.getView()).getUrl().contains("search?iptresource=acad-specimens"));
    }

    @Test
    public void testOccurrenceURL() throws Exception {
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.setMethod("GET");
    	request.setRequestURI("/resources/acad-specimens/occurrences/ACAD-2");
    	//test default view
    	Object handler = handlerMapping.getHandler(request).getHandler();    	
        ModelAndView mav = handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertViewName(mav,"occurrence");
        
        //using a dot in dwcaid
        response = new MockHttpServletResponse();
    	request = new MockHttpServletRequest();
    	request.setMethod("GET");
    	request.setRequestURI("/resources/trt-specimens/occurrences/TRT.6");
    	//test default view
    	handler = handlerMapping.getHandler(request).getHandler();    	
        mav = handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertViewName(mav,"occurrence");
        
        @SuppressWarnings("unchecked")
		HashMap<String,Object> modelRoot = (HashMap<String,Object>)mav.getModel().get(OccurrencePortalConfig.PAGE_ROOT_MODEL_KEY);
        OccurrenceModel occModel = (OccurrenceModel)modelRoot.get("occModel");
        assertEquals("TRT.6", occModel.getDwcaid());
    }
    
    /**
     * Test the model used for display purpose.
     * associatedSequences urls format are defined in src/main/resources/references/sequenceProviders.properties
     */
    @Test
    public void testOccurrenceViewModel(){
    	OccurrenceModel occModel = new OccurrenceModel();
    	occModel.setAssociatedsequences("BOLD :1234|bold: 2345|unknown:3456|http://bins.boldsystems.org/index.php/Public_BarcodeCluster?clusteruri=BOLD:AAJ7963");
    	
    	OccurrenceViewModel occViewModel = occurrenceController.buildOccurrenceViewModel(occModel, null, null, Locale.ENGLISH);
    	
    	//Map<String,List<Pair<String,String>>> associatedSequencesPerProviderMap = occViewModel.getAssociatedSequencesPerProviderMap();
    	List<String> associatedSequences = occViewModel.getAssociatedSequences();
    	assertEquals(4, associatedSequences.size());
    	
    	assertEquals("http://bins.boldsystems.org/index.php/Public_BarcodeCluster?clusteruri=BOLD:AAJ7963", associatedSequences.get(0));
    	assertEquals("http://www.boldsystems.org/connectivity/specimenlookup.php?processid=1234", associatedSequences.get(1));
    	assertEquals("http://www.boldsystems.org/connectivity/specimenlookup.php?processid=2345", associatedSequences.get(2));
    	assertEquals("unknown:3456", associatedSequences.get(3));
    }
    
}
