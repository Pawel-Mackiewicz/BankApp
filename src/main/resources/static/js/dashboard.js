// Main dashboard initialization module
import { FormManager, CreateAccountForm } from './modules/form-handlers.js';
import { DOMHelper } from './modules/dom-helper.js';
import { UIHelper } from './modules/validation.js';

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tab handling
    initializeTabs();
    
    // Initialize forms
    const formManager = new FormManager();
    const createAccountForm = new CreateAccountForm();

    // Initialize IBAN copy functionality
    DOMHelper.reattachIbanCopyListeners();
});

/**
 * Initialize tab functionality including form reset on tab change
 */
function initializeTabs() {
    const tabs = document.querySelectorAll('.nav-link[data-toggle="tab"]');
    tabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Update tab states
            tabs.forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(p => {
                p.classList.remove('show', 'active');
            });
            
            // Activate selected tab
            this.classList.add('active');
            const paneId = this.getAttribute('href').substring(1);
            const pane = document.getElementById(paneId);
            if (pane) {
                pane.classList.add('show', 'active');
            }
            
            // Reset all forms in tabs
            const forms = {
                own: document.getElementById('ownTransferForm'),
                internal: document.getElementById('internalTransferForm'),
                external: document.getElementById('externalTransferForm')
            };

            Object.values(forms).forEach(form => {
                if (form) {
                    form.reset();
                    UIHelper.resetValidation(form);
                    if (form.id === 'internalTransferForm') {
                        const ibanRadio = form.querySelector('input[value="iban"]');
                        if (ibanRadio) {
                            ibanRadio.checked = true;
                            DOMHelper.toggleRecipientInputs('iban');
                        }
                    }
                }
            });
        });
    });
}