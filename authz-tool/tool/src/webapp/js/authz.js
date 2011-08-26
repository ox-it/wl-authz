$(document).ready(function(){
	// When the checkboxes change update the cell.
	$('input:checkbox').change(function(){
		$(this).parents('td').toggleClass('active', this.checked);
	}).change();
    $("table.checkGrid tr:even").addClass("evenrow");
    // Save the default selected
    $(':checked').parents('td').addClass('defaultSelected');
    
    $('.permissionDescription').hover(function(e){
        $(this).parents('tr').children('td').toggleClass('rowHover', e.type == "mouseenter");
    });
    
    $('th').hover(function(event){
    	var col = ($(this).prevAll().size());
        $('.' + col).add(this).toggleClass('rowHover', event.type == "mouseenter");
    });
    
    $('th#permission').hover(function(event){
        $('.checkGrid td.checkboxCell').toggleClass('rowHover', event.type == "mouseenter");
    });
    
    $('th#permission a').click(function(e){
        $('.checkGrid input').attr('checked', ($('.checkGrid :checked').length > 0)?'':'checked').change();
        e.preventDefault();
    });
    $('.permissionDescription a').click(function(e){
    	var anyChecked = $(this).parents('tr').find('input:checked').length > 0;
    	$(this).parents('tr').find('input:checkbox').not('[disabled]').attr('checked', anyChecked?"":"checked").change();
        e.preventDefault();
    });
    $('th.role a').click(function(e){
        var col = ($(this).parent('th').prevAll().size());
        var anyChecked = $('.' + col + ' input:checked').length > 0;
        $('.' + col + ' input').not('[disabled]').attr('checked', anyChecked?"":"checked").change();
        e.preventDefault();
    });
    
    $('#clearall').click(function(e){
        $("input").attr("checked", "").change();
        e.preventDefault();
    });
    $('#restdef').click(function(e){
        $("input").attr("checked", "");
        $(".defaultSelected input").attr("checked", "checked").change();
        e.preventDefault();
    });
    
});
